package com.project.BankIt_backend.dashboard;

import com.project.BankIt_backend.account.Account;
import com.project.BankIt_backend.dashboard.dto.DashboardResponseDTO;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.account.AccountRepository;
import com.project.BankIt_backend.beneficiary.BeneficiaryRepository;
import com.project.BankIt_backend.transaction.TransactionRepository;
import com.project.BankIt_backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "data_analytics", key = "#userId")
    public DashboardResponseDTO getDashboard(Long userId) {
        System.out.println("DASHBOARD METHOD EXECUTED");
        //get list of accounts by the log-ed in user
        List<Account> accounts =
                accountRepository.findByUser_UserId(
                        userId
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


        //get the current user
        User currentUser = userService.getUserById(userId);

        //get the number of users beneficiary
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