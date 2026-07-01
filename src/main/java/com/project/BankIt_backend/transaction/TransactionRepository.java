package com.project.BankIt_backend.transaction;

import com.project.BankIt_backend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findBySenderAccount(Account senderAccountId);
    Optional<Transaction> findByReceiverAccount(Account receiverAccountId);

    List<Transaction> findBySenderAccount_User_Username(String username);

    Transaction findByTransactionId(Long transactionId);

    List<Transaction>
    findBySenderAccount_User_UsernameOrReceiverAccount_User_Username(
            String senderUsername,
            String receiverUsername
    );

    //for pagination in front-end
    Page<Transaction>
    findBySenderAccount_User_UsernameOrReceiverAccount_User_Username(
            String senderUsername,
            String receiverUsername,
            Pageable pageable
    );

    long countBySenderAccountInOrReceiverAccountIn(
            List<Account> senderAccounts,
            List<Account> receiverAccounts
    );

    // Calculates total money sent from the given accounts, returns 0 if no transactions exist.
    @Query("""
       SELECT COALESCE(SUM(t.amount),0)
       FROM Transaction t
       WHERE t.senderAccount IN :accounts
       """)
    BigDecimal getTotalSent(
            @Param("accounts")
            List<Account> accounts
    );

    // Calculates total money received from the given accounts; returns 0 if no transactions exist.
    @Query("""
       SELECT COALESCE(SUM(t.amount),0)
       FROM Transaction t
       WHERE t.receiverAccount IN :accounts
       """)
    BigDecimal getTotalReceived(
            @Param("accounts")
            List<Account> accounts
    );

    //takes a userId and a date range (startDate to endDate), pulls every transaction
    // where that user was involved as either the sender or receiver during
    // that time window, sorts them from newest to oldest, and returns them
    // as a clean List<Transaction>

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE
        (
            t.senderAccount.user.userId = :userId
            OR
            t.receiverAccount.user.userId = :userId
        )
        AND
        t.transactionDate BETWEEN :startDate AND :endDate
        ORDER BY t.transactionDate DESC
    """)
    List<Transaction> getStatementTransactions(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT t
        FROM Transaction t
        JOIN FETCH t.senderAccount
        JOIN FETCH t.receiverAccount
        WHERE t.transactionId = :id
    """)
    Optional<Transaction> findByIdWithAccounts(Long id);

    List<Transaction> findTop10BySenderAccountOrderByTransactionDateDesc(Account senderAccount);

    List<Transaction> findBySenderAccountAndTransactionDateAfter(Account account, LocalDateTime time);
}
