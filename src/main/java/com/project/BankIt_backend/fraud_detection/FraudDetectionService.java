package com.project.BankIt_backend.fraud_detection;

import com.project.BankIt_backend.account.Account;
import com.project.BankIt_backend.common.enums.FraudRule;
import com.project.BankIt_backend.common.enums.FraudSeverity;
import com.project.BankIt_backend.common.enums.FraudStatus;
import com.project.BankIt_backend.transaction.Transaction;
import com.project.BankIt_backend.transaction.TransactionRepository;
import com.project.BankIt_backend.transaction.TransactionService;
import com.project.BankIt_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudAlertRepository  fraudAlertRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal LOW_VALUE_DRAINED = new BigDecimal("30000");
    private static final BigDecimal ACCOUNT_DRAIN_PERCENTAGE = new BigDecimal("0.90");
    private static final int RAPID_TRANSFER_THRESHOLD = 5;
    private static final long RAPID_TRANSFER_WINDOW_MINUTES = 10;
    private static final BigDecimal UNUSUAL_AMOUNT_MULTIPLIER = new BigDecimal("3");

    public void analyze(Transaction transaction) {
        checkHighValueTransaction(transaction);

        checkRapidTransfers(transaction);

        checkAccountDraining(transaction);

        checkUnusualAmount(transaction);
    }


    private void checkUnusualAmount(Transaction transaction) {

        List<Transaction> previousTransactions =
                transactionRepository.findTop10BySenderAccountOrderByTransactionDateDesc(
                        transaction.getSenderAccount()
                );

        if (previousTransactions.size() < 5) {
            return;
        }

        BigDecimal totalAmount = previousTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageAmount = totalAmount.divide(
                BigDecimal.valueOf(previousTransactions.size()),
                2,
                RoundingMode.HALF_UP
        );

        if (transaction.getAmount()
                .compareTo(averageAmount.multiply(UNUSUAL_AMOUNT_MULTIPLIER)) > 0) {

            String description = String.format(
                    "Transaction amount %s is significantly higher than the average transaction amount of %s.",
                    transaction.getAmount(),
                    averageAmount
            );

            createFraudAlert(
                    transaction,
                    FraudRule.UNUSUAL_AMOUNT,
                    FraudSeverity.MEDIUM,
                    description
            );
        }
    }

    private void checkRapidTransfers(Transaction currentTransaction) {
        Account senderAccount = currentTransaction.getSenderAccount();

        LocalDateTime tenMinutesAgo =
                currentTransaction.getTransactionDate().minusMinutes(RAPID_TRANSFER_WINDOW_MINUTES);

        List<Transaction> recentTransfers = transactionRepository.findBySenderAccountAndTransactionDateAfter(
                                senderAccount,
                                tenMinutesAgo
                        );

        String description = String.format(
                "%d transfers were made within the last 10 minutes.",
                recentTransfers.size()
        );

        if( recentTransfers.size() >= RAPID_TRANSFER_THRESHOLD ){

            createFraudAlert(currentTransaction,
                    FraudRule.RAPID_TRANSFERS,
                    FraudSeverity.HIGH,
                    description
            );

        }

    }

    private void checkAccountDraining(Transaction transaction) {

        BigDecimal currentBalance =
                transaction.getSenderAccount().getBalance();

        BigDecimal originalBalance =
                currentBalance.add(transaction.getAmount());

        BigDecimal percentageTransferred =
                transaction.getAmount()
                        .divide(originalBalance, 4, RoundingMode.HALF_UP);

        if (currentBalance.compareTo(LOW_VALUE_DRAINED) <= 0 &&
                percentageTransferred.compareTo(ACCOUNT_DRAIN_PERCENTAGE) >= 0) {

            String description = String.format(
                    "Transferred %.2f%% of the account balance, leaving only %s remaining.",
                    percentageTransferred.multiply(new BigDecimal("100")),
                    currentBalance
            );

            createFraudAlert(
                    transaction,
                    FraudRule.ACCOUNT_DRAINING,
                    FraudSeverity.HIGH,
                    description
            );
        }
    }

    private void checkHighValueTransaction(Transaction transaction) {
        String description = String.format(
                "Transaction amount %s exceeded the threshold of %s.",
                transaction.getAmount(),
                HIGH_VALUE_THRESHOLD
        );

        if(transaction.getAmount().compareTo(HIGH_VALUE_THRESHOLD) > 0) {

            createFraudAlert(
                    transaction,
                    FraudRule.HIGH_VALUE_TRANSACTION,
                    FraudSeverity.HIGH,
                    description
            );
        }
    }

    private void createFraudAlert(
            Transaction transaction,
            FraudRule rule,
            FraudSeverity severity,
            String description) {

        FraudAlert fraudAlert = FraudAlert.builder()
                .transaction(transaction)
                .ruleTriggered(rule)
                .severity(severity)
                .status(FraudStatus.OPEN)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        fraudAlertRepository.save(fraudAlert);
    }

}
