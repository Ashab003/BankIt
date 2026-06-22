package com.project.BankIt_backend.beneficiary;

import com.project.BankIt_backend.beneficiary.dto.BeneficiaryAddRequestDTO;
import com.project.BankIt_backend.beneficiary.dto.BeneficiaryAddResponseDTO;
import com.project.BankIt_backend.beneficiary.dto.BeneficiarySearchResponseDTO;
import com.project.BankIt_backend.beneficiary.dto.MyBeneficiaryResponseDTO;
import com.project.BankIt_backend.account.Account;
import com.project.BankIt_backend.audit.AuditLogService;
import com.project.BankIt_backend.common.exception.*;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.common.enums.AuditAction;
import com.project.BankIt_backend.account.AccountRepository;
import com.project.BankIt_backend.user.UserRepository;
import com.project.BankIt_backend.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;
    @Autowired
    private CacheManager cacheManager;

    @Transactional
    public BeneficiaryAddResponseDTO addBeneficiary(BeneficiaryAddRequestDTO dto){
        Account recieverAccount = accountRepository
                .findByAccountNo(
                        dto.getAccountNumber()
                )
                .orElseThrow(
                        ()->new AccountNotFoundException("ACCOUNT INVALID")
                );
        User user = userService.getCurrentUser();
        String username = user.getUsername();

        User currentUser = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        //cant add itself
        if(currentUser.getUserId().equals(
                recieverAccount.getUser().getUserId())) {

            throw new InvalidBeneficiaryException(
                    "The entered account belongs to you. Please enter a different account number.");
        }

        beneficiaryRepository
                .findByUser_UserIdAndBeneficiaryAccount_AccountId(
                        currentUser.getUserId(),
                        recieverAccount.getAccountId()
                )
                .ifPresent(b ->
                {
                    throw new BeneficiaryAlreadyExists(
                            "This beneficiary has already been added to your account.");
                });

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(currentUser);
        beneficiary.setBeneficiaryAccount(recieverAccount);
        beneficiary.setNickName(null);
        beneficiary.setCreatedAt(LocalDateTime.now());
        beneficiaryRepository.save(beneficiary);

        auditLogService.logAction(
                currentUser,
                AuditAction.BENEFICIARY_ADDED,
                LocalDateTime.now(),
                "Added beneficiary account: " + beneficiary.getBeneficiaryAccount().getAccountNo()
        );


        cacheManager
                .getCache("data_analytics")
                .evict(user.getUserId());

        return new BeneficiaryAddResponseDTO(
                beneficiary.getBeneficiaryId(),
                beneficiary.getBeneficiaryAccount()
                        .getUser()
                        .getFullName(),
                beneficiary.getBeneficiaryAccount()
                        .getAccountNo(),
                beneficiary.getCreatedAt()
        );
    }

    public List<MyBeneficiaryResponseDTO> getMyBeneficiaries() {

        User currentUser = userService.getCurrentUser();

        return beneficiaryRepository
                .findByUser_UserId(currentUser.getUserId())
                .stream()
                .map(this::convertToMyDTO)
                .toList();
    }

    public void removeBeneficiary(Long beneficiaryId) {

        User currentUser = userService.getCurrentUser();

        Beneficiary beneficiary = beneficiaryRepository
                .findById(beneficiaryId)
                .orElseThrow(() ->
                        new BeneficiaryNotFoundException("Beneficiary not found"));

        if (!beneficiary.getUser().getUserId()
                .equals(currentUser.getUserId())) {

            throw new BeneficiaryUnauthorizedAccessException(
                    "You are not authorized to delete this beneficiary.");
        }

        beneficiaryRepository.delete(beneficiary);

        auditLogService.logAction(
                currentUser,
                AuditAction.BENEFICIARY_REMOVED,
                LocalDateTime.now(),
                "Removed beneficiary: " + beneficiary.getUser().getUsername()
        );
    }

    private BeneficiaryAddResponseDTO convertToDTO(Beneficiary beneficiary) {

        return new BeneficiaryAddResponseDTO(
                beneficiary.getBeneficiaryId(),
                beneficiary.getBeneficiaryAccount()
                        .getUser()
                        .getFullName(),
                beneficiary.getBeneficiaryAccount()
                        .getAccountNo(),
                beneficiary.getCreatedAt()
        );
    }

    private MyBeneficiaryResponseDTO convertToMyDTO(Beneficiary beneficiary) {

        return new MyBeneficiaryResponseDTO(
                beneficiary.getBeneficiaryId(), // ✅ CORRECT
                beneficiary.getBeneficiaryAccount().getUser().getFullName(),
                beneficiary.getBeneficiaryAccount().getUser().getEmail(),
                beneficiary.getBeneficiaryAccount().getUser().getPhoneNumber(),
                beneficiary.getBeneficiaryAccount().getAccountNo()
        );
    }

    public MyBeneficiaryResponseDTO getBeneficiary(Long beneficiaryId) {
        User currentUser = userService.getCurrentUser();

        Beneficiary beneficiary = beneficiaryRepository
                .findById(beneficiaryId)
                .orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiary not found"));

        // Security check
        if (!beneficiary.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BeneficiaryUnauthorizedAccessException("You are not authorized to access this beneficiary");
        }

        User beneficiaryUser = beneficiary
                .getBeneficiaryAccount()
                .getUser();

        return new MyBeneficiaryResponseDTO(
                beneficiary.getBeneficiaryId(),
                beneficiaryUser.getFullName(),
                beneficiaryUser.getEmail(),
                beneficiaryUser.getPhoneNumber(),
                beneficiary.getBeneficiaryAccount().getAccountNo()
        );
    }

    public BeneficiarySearchResponseDTO findUserByAccountNumber(String accountNumber) {

        Account account = accountRepository
                .findByAccountNo(accountNumber)
                .orElseThrow(() ->
                        new InvalidBeneficiaryException("Account not found"));

        User user = account.getUser();

        User currentUser = userService.getCurrentUser();

        if (account.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You cannot add yourself as beneficiary");
        }

        return new BeneficiarySearchResponseDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                account.getAccountNo()
        );
    }
}
