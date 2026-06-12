package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.BeneficiaryRequestDTO;
import com.project.BankIt_backend.dto.BeneficiaryResponseDTO;
import com.project.BankIt_backend.dto.MyBeneficiaryResponseDTO;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.Beneficiary;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.BeneficiaryRepository;
import com.project.BankIt_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public BeneficiaryResponseDTO addBeneficiary(BeneficiaryRequestDTO dto){
        Account recieverAccount = accountRepository
                .findByAccountNo(
                        dto.getAccountNumber()
                )
                .orElseThrow(
                        ()->new RuntimeException("ACCOUNT INVALID")
                );
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        //cant add itself
        if(currentUser.getUserId().equals(
                recieverAccount.getUser().getUserId())) {

            throw new RuntimeException(
                    "Cannot add yourself as beneficiary");
        }

        beneficiaryRepository
                .findByUser_UserIdAndBeneficiaryAccount_AccountId(
                        currentUser.getUserId(),
                        recieverAccount.getAccountId()
                )
                .ifPresent(b ->
                {
                    throw new RuntimeException(
                            "Beneficiary already exists");
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
                "Added beneficiary account: " + beneficiary.getBeneficiaryAccount().getAccountNo()
        );

        return new BeneficiaryResponseDTO(
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
                        new RuntimeException("Beneficiary not found"));

        if (!beneficiary.getUser().getUserId()
                .equals(currentUser.getUserId())) {

            throw new RuntimeException(
                    "You cannot delete another user's beneficiary");
        }

        beneficiaryRepository.delete(beneficiary);

        auditLogService.logAction(
                currentUser,
                AuditAction.BENEFICIARY_REMOVED,
                "Removed beneficiary: " + beneficiary.getUser().getUsername()
        );
    }

    private BeneficiaryResponseDTO convertToDTO(Beneficiary beneficiary) {

        return new BeneficiaryResponseDTO(
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
                beneficiary.getBeneficiaryAccount().getAccountId(),
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
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));

        // Security check
        if (!beneficiary.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You are not authorized to access this beneficiary");
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
}
