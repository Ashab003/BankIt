package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.RegisterRequestDTO;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Account createAccount(User user){
        Account account = new Account();
        account.setAccountNo(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());
        account.setCurrency("INR");
        account.setStatus("ACTIVE");

        return account;
    }

    public String generateAccountNumber(){
        return String.valueOf((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);
    }

    public Optional<Account> getDetails(String accountNo){
        return Optional.of(
                accountRepository.findByAccountNo(accountNo)
                        .orElseThrow(
                                () -> new RuntimeException("Account wiht this Id not found")
                        )
        );
    }

    public BigDecimal getCurrentBalance(String accountNo){
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getBalance();
    }
}
