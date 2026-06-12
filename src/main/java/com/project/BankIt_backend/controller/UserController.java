package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.BeneficiarySearchResponseDTO;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.ok(
                "User marked as deleted successfully"
        );
    }
}
