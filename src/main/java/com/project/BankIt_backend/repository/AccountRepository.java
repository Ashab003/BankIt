package com.project.BankIt_backend.repository;

import com.project.BankIt_backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser_UserId(Long userId);
    Optional<Account> findByAccountNo(String accountNo);
}
