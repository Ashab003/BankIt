package com.project.BankIt_backend.service;
import com.project.BankIt_backend.dto.TransactionResponseDTO;
import com.project.BankIt_backend.dto.TransferRequestDTO;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.Transaction;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.TransactionRepository;
import com.project.BankIt_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    private void verifySenderAccount(Account senderAccount, BigDecimal amount) {
        //✓ Sender account ACTIVE
        if (senderAccount == null) {
            throw new RuntimeException("Sender account not found");
        }

        //✓ Receiver account ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(senderAccount.getStatus())) {
            throw new RuntimeException("Sender account is not active");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid transfer amount");
        }
        //✓ Sender balance >= amount

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
    }

    private void verifyReceiverAccount(Account receiverAccount) {
        //receiverAccount  exists
        if (receiverAccount == null) {
            throw new RuntimeException("Receiver account not found");
        }

        //✓ Receiver account ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(receiverAccount.getStatus())) {
            throw new RuntimeException("Receiver account is not active");
        }
    }

    @Transactional
    public TransactionResponseDTO transferMoney(TransferRequestDTO transferRequestDTO) {

        // 1. Get logged-in user
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User senderUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Get sender account
        Account senderAccount = accountRepository
                .findByUser_UserId(senderUser.getUserId())
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Sender account not found"));

        // 3. Verify sender
        verifySenderAccount(senderAccount, transferRequestDTO.getAmount());

        // 4. Find receiver user
        User receiverUser = userRepository
                .findByEmailOrPhoneNumber(
                        transferRequestDTO.getRecipientIdentifier(),
                        transferRequestDTO.getRecipientIdentifier()
                )
                .orElseThrow(() ->
                        new RuntimeException("Receiver not found"));

        // 5. Get receiver account
        Account receiverAccount = accountRepository
                .findByUser_UserId(receiverUser.getUserId())
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Receiver account not found"));

        // 6. Verify receiver
        verifyReceiverAccount(receiverAccount);

        //Cant send money to yourself
        if (senderUser.getUserId().equals(receiverUser.getUserId())) {
            throw new RuntimeException("Cannot transfer money to yourself");
        }

        // 7. Transfer money
        senderAccount.setBalance(
                senderAccount.getBalance()
                        .subtract(transferRequestDTO.getAmount())
        );

        receiverAccount.setBalance(
                receiverAccount.getBalance()
                        .add(transferRequestDTO.getAmount())
        );

        // 8. Save both accounts
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        //9. Save Transaction
        Transaction transaction = saveTransaction(senderAccount, receiverAccount, transferRequestDTO);

        auditLogService.logAction(
                senderUser,
                AuditAction.MONEY_TRANSFERRED,
                "Transferred ₹" + transferRequestDTO.getAmount() +
                        " to account " + receiverAccount.getAccountNo()
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

    public Transaction saveTransaction(
            Account senderAccount,
            Account receiverAccount,
            TransferRequestDTO transferRequestDTO) {

        Transaction transaction = new Transaction();

        transaction.setSenderAccount(senderAccount);

        transaction.setReceiverAccount(receiverAccount);

        transaction.setAmount(
                transferRequestDTO.getAmount()
        );

        transaction.setTransactionType("TRANSFER");

        transaction.setStatus("SUCCESS");

        transaction.setReferenceNumber(
                "CR7-" + System.currentTimeMillis()
        );

        transaction.setDescription(
                transferRequestDTO.getDescription()
        );

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        return transactionRepository.save(transaction);
    }
}
