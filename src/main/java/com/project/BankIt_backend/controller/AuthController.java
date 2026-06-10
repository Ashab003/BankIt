package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.LoginRequestDTO;
import com.project.BankIt_backend.dto.LoginResponseDTO;
import com.project.BankIt_backend.dto.RegisterRequestDTO;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import com.project.BankIt_backend.exception.AccountInactiveException;
import com.project.BankIt_backend.service.AuditLogService;
import com.project.BankIt_backend.service.AuthService;
import com.project.BankIt_backend.service.JwtService;
import com.project.BankIt_backend.service.UserService;
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
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuditLogService auditLogService;

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
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        //validate username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        //get user
        User user = userService.getUserByUsernameOrEmail(
                request.getUsernameOrEmail()
        );

        //check if user is active
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {

            throw new AccountInactiveException(
                    "Account is not active"
            );
        }

        //generate jwt token
        var userDetails =
                authService.loadUserByUsername(
                        request.getUsernameOrEmail()
                );

        var jwtToken =
                jwtService.generateToken(userDetails);

        //save auditlog
        auditLogService.logAction(
                user,
                AuditAction.USER_LOGIN,
                "User logged in successfully"
        );

        //provide response
        LoginResponseDTO response =
                new LoginResponseDTO(
                        jwtToken,
                        "Login Successful"
                );

        return ResponseEntity.ok(response);
    }
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