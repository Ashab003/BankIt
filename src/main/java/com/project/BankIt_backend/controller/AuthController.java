package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.LoginRequestDTO;
import com.project.BankIt_backend.dto.LoginResponseDTO;
import com.project.BankIt_backend.dto.RegisterRequestDTO;
import com.project.BankIt_backend.service.AuthService;
import com.project.BankIt_backend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        // 1. Authenticate using the combined username/email field
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // 2. Load the user details
        var userDetails = authService.loadUserByUsername(request.getUsernameOrEmail());
        var jwtToken = jwtService.generateToken(userDetails);

        // 3. Return the proper Response DTO
        LoginResponseDTO response = new LoginResponseDTO(jwtToken, "LOGIN!!");

        return ResponseEntity.ok(response);
    }

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