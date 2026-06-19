package com.project.BankIt_backend.account;


import com.project.BankIt_backend.account.dto.AccountResponseDTO;
import com.project.BankIt_backend.account.dto.BalanceResponseDTO;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.user.UserRepository;
import com.project.BankIt_backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AccountService accountService;

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

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDTO> balance() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                accountService.getBalance(
                        user.getUserId()
                )
        );
    }
}



