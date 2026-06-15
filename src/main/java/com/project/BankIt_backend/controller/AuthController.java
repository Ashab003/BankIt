package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.LoginRequestDTO;
import com.project.BankIt_backend.dto.LoginResponseDTO;
import com.project.BankIt_backend.dto.RegisterRequestDTO;
import com.project.BankIt_backend.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.registerUser(dto));
    }

    // SECURE ENDPOINT: Only users with the "ADMIN" role can access this
    @PostMapping("/register-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registerAdminUser(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.registerAdmin(dto));
    }

    //---------------------------------------------------------------------------------

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //LOGOUT is already implemented by LOGOUT HANDLER

    //---------------------------------------------------------------------------------------
    @GetMapping("/me")
    public String currentUser(Authentication authentication) {
        return """
            Username: %s
            Authorities: %s
            """.formatted(
                authentication.getName(),
                authentication.getAuthorities()
        );
    }

}