package com.project.BankIt_backend.auth;

import com.project.BankIt_backend.auth.dto.LoginRequestDTO;
import com.project.BankIt_backend.auth.dto.LoginResponseDTO;
import com.project.BankIt_backend.auth.dto.RegisterRequestDTO;
import com.project.BankIt_backend.user.UserService;
import com.project.BankIt_backend.user.dto.ChangePasswordRequestDTO;
import com.project.BankIt_backend.user.dto.ChangePasswordResponseDTO;
import com.project.BankIt_backend.user.dto.FullNameResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final UserService userService;

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

    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        return ResponseEntity.ok(
                authService.changePassword(request)
        );
    }


    @GetMapping("/me")
    public ResponseEntity<FullNameResponseDTO> currentUser() {
        return ResponseEntity.ok(
                userService.getUserFullName()
        );
    }

}