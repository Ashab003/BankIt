package com.project.BankIt_backend.repository;

import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findBySenderAccount(Account senderAccountId);
    Optional<Transaction> findByReceiverAccount(Account receiverAccountId);

    List<Transaction> findBySenderAccount_User_Username(String username);

    List<Transaction>
    findBySenderAccount_User_UsernameOrReceiverAccount_User_Username(
            String senderUsername,
            String receiverUsername
    );
}
