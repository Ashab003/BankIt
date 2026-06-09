package com.project.BankIt_backend.controller;


import com.project.BankIt_backend.dto.AccountResponseDTO;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountResponseDTO>> accountDetails(
            @PathVariable Long userId) {
        List<Account> accounts = accountRepository.findByUser_UserId(userId);

        if (accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<AccountResponseDTO> response =
                accounts.stream()
                        .map(a -> new AccountResponseDTO(
                                a.getAccountNo(),
                                a.getBalance(),
                                a.getCurrency(),
                                a.getStatus()))
                        .toList();

        return ResponseEntity.ok(response);
    }

}



