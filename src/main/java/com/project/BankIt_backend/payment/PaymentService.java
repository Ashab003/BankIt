package com.project.BankIt_backend.payment;
import com.project.BankIt_backend.account.AccountService;
import com.project.BankIt_backend.audit.AuditLogService;
import com.project.BankIt_backend.common.enums.NotificationType;
import com.project.BankIt_backend.common.enums.RequestStatus;
import com.project.BankIt_backend.common.exception.*;
import com.project.BankIt_backend.fraud_detection.FraudAlert;
import com.project.BankIt_backend.fraud_detection.FraudDetectionService;
import com.project.BankIt_backend.notification.NotificationService;
import com.project.BankIt_backend.notification.dto.NotificationDTO;
import com.project.BankIt_backend.payment.dto.*;
import com.project.BankIt_backend.account.Account;
import com.project.BankIt_backend.transaction.Transaction;
import com.project.BankIt_backend.transaction.TransactionService;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.common.enums.AuditAction;
import com.project.BankIt_backend.account.AccountRepository;
import com.project.BankIt_backend.transaction.TransactionRepository;
import com.project.BankIt_backend.user.UserRepository;
import com.project.BankIt_backend.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;
    private final CacheManager cacheManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final PaymentRequestRepository paymentRequestRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final FraudDetectionService fraudDetectionService;

    private void verifySenderAccount(Account senderAccount, BigDecimal amount) {
        //checking if Sender account ACTIVE
        if (senderAccount == null) {
            throw new AccountNotFoundException("Sender account not found");
        }

        //checking if Receiver account ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(senderAccount.getStatus())) {
            throw new AccountInactiveException("Sender account is not active");
        }

        //checking if amount entered is correct
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferAmountException("Invalid transfer amount");
        }

        //checking if sender has enough money to send
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds to complete this transaction.");
        }
    }

    private void verifyReceiverAccount(Account receiverAccount) {
        //checking if receiverAccount  exists
        if (receiverAccount == null) {
            throw new AccountNotFoundException("Receiver account not found");
        }

        //checking if Receiver account ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(receiverAccount.getStatus())) {
            throw new AccountInactiveException("Receiver account is not active");
        }
    }

    @Transactional
    public TransactionResponseDTO transferMoney(
            TransferRequestDTO dto
    ) {

        //get the sender
        User senderUser =
                userService.getCurrentUser();

        //get the receiver
        User receiverUser =
                userService
                        .getUserByUsernameOrEmail(
                                dto.getRecipientIdentifier()
                        );

        //now here is the main part where transfer of money is happening and the tables are being effect here
        Transaction transaction =
                performTransfer(
                        senderUser,
                        receiverUser,
                        dto.getAmount(),
                        dto.getDescription()
                );

        String notificationMessage = dto.getAmount() + "INR credited into your account from " + senderUser.getFullName();
        notificationService.createNotification(
                senderUser,
                receiverUser,
                "Money Received",
                notificationMessage,
                NotificationType.MONEY_RECEIVED
        );

        return new TransactionResponseDTO(
                transaction.getTransactionId(),
                transaction.getSenderAccount().getAccountNo(),
                transaction.getReceiverAccount().getAccountNo(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getReferenceNumber(),
                transaction.getDescription(),
                transaction.getTransactionDate()
        );
    }

    @Transactional
    public Transaction performTransfer(
            User senderUser,
            User receiverUser,
            BigDecimal amount,
            String description
    ) {

        //get the senders account
        Account senderAccount =
                accountService.getUserAccount(senderUser);

        //get the receivers account
        Account receiverAccount =
                accountService.getUserAccount(receiverUser);

        //verify the sender account
        verifySenderAccount(
                senderAccount,
                amount
        );

        //verify the receiver account
        verifyReceiverAccount(
                receiverAccount
        );

        //checking here if the sender and receiver are not the same
        if(senderUser.getUserId()
                .equals(receiverUser.getUserId())) {

            throw new IllegalArgumentException(
                    "Cannot transfer money to yourself"
            );
        }

        //main stuff here, money deducted from sender and added to receiver
        senderAccount.setBalance(
                senderAccount.getBalance()
                        .subtract(amount)
        );
        receiverAccount.setBalance(
                receiverAccount.getBalance()
                        .add(amount)
        );

        //saving the effects account
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        //saving the transaction
        Transaction transaction =
                transactionService.saveTransaction(
                        senderAccount,
                        receiverAccount,
                        amount,
                        description
                );

        //loggint the action of money transfer
        auditLogService.logAction(
                senderUser,
                AuditAction.MONEY_TRANSFERRED,
                LocalDateTime.now(),
                "Transferred ₹" + amount +
                        " to account " +
                        receiverAccount.getAccountNo()
        );

        fraudDetectionService.analyze(transaction);

        //as transaction has happened so cached data is old now so we remove the old data
        evictUserCaches(senderUser);
        evictUserCaches(receiverUser);

        return transaction;
    }

    public User sendRequest(SendRequestDTO requestDTO){
        //sending request is basically creating record in Payment req db for a request
        //requester id is the user logged in

        String requestedTo = requestDTO.getRecipientIdentifier();
        BigDecimal amount = requestDTO.getAmount();
        String note = requestDTO.getNote();

        User requestedUser = userService.getUserByUsernameOrEmail(requestedTo);
        User requestingUser = userService.getCurrentUser();


        //just checking the users here
        if(requestedUser == null){
            throw new IllegalArgumentException("User not found");
        }

        if(requestingUser.getUserId().equals(requestedUser.getUserId())){
            throw new IllegalArgumentException("You cannot request money from yourself");
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }


        PaymentRequest paymentRequest = new PaymentRequest();

        //updating the PAYMENT_REQUEST database
        paymentRequest.setRequester(requestingUser);
        paymentRequest.setRequestedFrom(requestedUser);
        paymentRequest.setAmount(amount);
        paymentRequest.setNote(note);
        paymentRequest.setStatus(RequestStatus.PENDING);
        paymentRequest.setCreatedAt(LocalDateTime.now());

        paymentRequestRepository.save(paymentRequest);

        //notifying the requested user
        String notificationMessage = requestingUser.getFullName()
                + " requested ₹"
                + amount
                + " from you.";

        notificationService.createNotification(
                requestingUser,
                requestedUser,
                "Payment Request",
                notificationMessage,
                NotificationType.PAYMENT_REQUEST
        );


        //this method returns user which will give us the money
        return requestedUser;
    }

    public List<IncomingRequest_ResponseDTO> incomingRequest(User currentUser){

        return paymentRequestRepository
                .findAllByRequestedFromAndStatus(
                        currentUser,
                        RequestStatus.PENDING
                )
                .stream()
                .map(this::mapToIncomingRequest_ResponseDTO)
                .toList();
    }

    private IncomingRequest_ResponseDTO mapToIncomingRequest_ResponseDTO(PaymentRequest request){

        return new IncomingRequest_ResponseDTO(
                request.getRequestId(),
                request.getRequester().getFullName(),
                request.getNote(),
                request.getCreatedAt(),
                request.getAmount(),
                request.getStatus().name()
        );
    }

    @Transactional
    public PaymentRequest approveRequest(Long requestId) {

        //getting the request and checking if request is there
        PaymentRequest request =
                paymentRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new RequestNotFoundException(
                                        "Request not found"
                                ));

        //checking if request showed is still in pending status, if it is not then it is already processed
        if(request.getStatus()
                != RequestStatus.PENDING) {

            throw new RequestAlreadyProcessedException(
                    "Request already processed"
            );
        }

        User currentUser = userService.getCurrentUser();


        //checking if request is not from the user itself
        if(!request.getRequestedFrom()
                .getUserId()
                .equals(currentUser.getUserId())) {

            throw new UnauthorizedAccessException(
                    "You are not allowed to approve this request"
            );
        }

        //main stuff here where transfer of money happens
        performTransfer(
                request.getRequestedFrom(),
                request.getRequester(),
                request.getAmount(),
                request.getNote()
        );

        //as the money is transferred now the status should be changed to approved
        request.setStatus(
                RequestStatus.APPROVED
        );

        //saving when the request was approved
        request.setRespondedAt(
                LocalDateTime.now()
        );

        //saving the request
        paymentRequestRepository
                .save(request);

        //notifying the receiver
        String notificationMessage =  currentUser.getFullName()
                + " approved your payment request of ₹"
                + request.getAmount();

        notificationService.createNotification(
                currentUser,
                request.getRequester(),
                "Request Approved",
                notificationMessage,
                NotificationType.PAYMENT_REQUEST_APPROVED
        );


        return request;
    }

    @Transactional
    public PaymentRequest rejectRequest(Long requestId) {

        //getting the request and checking if request is there
        PaymentRequest request =
                paymentRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new RequestNotFoundException(
                                        "Request not found"
                                ));

        //checking if request showed is still in pending status, if it is not then it is already processed
        if(request.getStatus()
                != RequestStatus.PENDING) {

            throw new RequestAlreadyProcessedException(
                    "Request already processed"
            );
        }

        User currentUser = userService.getCurrentUser();


        //checking if request is not from the user itself
        if(!request.getRequestedFrom()
                .getUserId()
                .equals(currentUser.getUserId())) {

            throw new UnauthorizedAccessException(
                    "You are not allowed to approve this request"
            );
        }

        //setting status to rejected
        request.setStatus(
                RequestStatus.REJECTED
        );

        request.setRespondedAt(
                LocalDateTime.now()
        );

        //saving the request
        paymentRequestRepository
                .save(request);

        //notifying the receiver
        String notificationMessage =  request.getRequester().getFullName() + " has rejected your request to pay you INR" + request.getAmount();
        notificationService.createNotification(
                userService.getCurrentUser(),
                request.getRequester(),
                "Request Rejected",
                notificationMessage,
                NotificationType.PAYMENT_REQUEST_REJECTED
        );

        return request;
    }

    public RequestStats_ResponseDTO countOfStatus(){
        User user = userService.getCurrentUser();
        long userid = user.getUserId();

        long  approvedCount = paymentRequestRepository.countByStatusAndRequester_UserId(RequestStatus.APPROVED, userid);
        long  pendingCount = paymentRequestRepository.countByStatusAndRequester_UserId(RequestStatus.PENDING, userid);
        long rejectedCount = paymentRequestRepository.countByStatusAndRequester_UserId(RequestStatus.REJECTED, userid);

        RequestStats_ResponseDTO requestStatsResponseDTO = new RequestStats_ResponseDTO();

        requestStatsResponseDTO.setApproveCount(approvedCount);
        requestStatsResponseDTO.setPendingCount(pendingCount);
        requestStatsResponseDTO.setRejectCount(rejectedCount);

        return  requestStatsResponseDTO;
    }

    //special one, it is handling the cache, preventing old data in cache
    private void evictUserCaches(User user) {

        cacheManager
                .getCache("data_analytics")
                .evict(user.getUserId());

        cacheManager
                .getCache("balance")
                .evict(user.getUserId());
    }

    public List<OutgoingRequest_ResponseDTO> listOfOutgoingRequest(){

        User requester = userService.getCurrentUser();
        return paymentRequestRepository
                .findAllByRequester(requester)
                .stream()
                .map(this::maptoOutgoingRequest_ResponseDTO)
                .toList();
    }

    public OutgoingRequest_ResponseDTO maptoOutgoingRequest_ResponseDTO(PaymentRequest request){
        return new OutgoingRequest_ResponseDTO(
                request.getRequestId(),
                request.getRequestedFrom().getFullName(),
                request.getAmount(),
                request.getStatus(),
                request.getNote(),
                request.getCreatedAt()
        );
    }
}


