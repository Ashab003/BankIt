package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.DashboardResponseDTO;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.BeneficiaryRepository;
import com.project.BankIt_backend.repository.TransactionRepository;
import com.project.BankIt_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserService userService;

    private final AccountRepository accountRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final TransactionRepository transactionRepository;

    public DashboardResponseDTO getDashboard() {

        //get current user
        User currentUser = userService.getCurrentUser();

        //get list of accounts by the log-ed in user
        List<Account> accounts =
                accountRepository.findByUser_UserId(
                        currentUser.getUserId()
                );

        //counts total transactions
        Long transactionCount =
                transactionRepository
                        .countBySenderAccountInOrReceiverAccountIn(
                                accounts,
                                accounts
                        );

        //total money sent from a specific list of accounts
        BigDecimal moneySent =
                transactionRepository
                        .getTotalSent(accounts);

        //total money received
        BigDecimal moneyReceived =
                transactionRepository
                        .getTotalReceived(accounts);

        //get balance from every account and add up to (for now there is only one account per user but multiple accounts are possible)
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //get the number of users
        Long beneficiaryCount =
                beneficiaryRepository.countByUser_UserId(
                        currentUser.getUserId()
                );

        return new DashboardResponseDTO(
                totalBalance,
                beneficiaryCount,
                transactionCount,
                moneySent,
                moneyReceived
        );
    }
}