package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.LoginRequestDTO;
import com.project.BankIt_backend.dto.LoginResponseDTO;
import com.project.BankIt_backend.dto.RegisterRequestDTO;
import com.project.BankIt_backend.entity.Role;
import com.project.BankIt_backend.entity.Token;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import com.project.BankIt_backend.enums.TokenType;
import com.project.BankIt_backend.exception.AccountInactiveException;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.RoleRepository;
import com.project.BankIt_backend.repository.TokenRepository;
import com.project.BankIt_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final AuditLogService auditLogService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    //FOR REGULAR USER
    public Optional<User> registerUser(RegisterRequestDTO userDTO) {

        // Initializing a blank User Entity object
        User user = new User();

        // Mapping data from DTO to the Entity
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        // Securely setting the password using the PasswordEncoder bean
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // setting user status and time
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        // Fetch the existing role from database
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role not found in database."));

        // Link the role to this specific user object
        // (Assuming you initialized 'roles = new HashSet<>()' in your User entity)
        user.getRoles().add(customerRole);

        // Explicitly calling the repository to insert and generate id and save the user in db
        User savedUser = userRepository.save(user);

        accountRepository.save(accountService.createAccount(savedUser));

        //save log
        auditLogService.logAction(
                savedUser,
                AuditAction.USER_REGISTERED,
                "New user registered with username: " + savedUser.getUsername()
        );

        return Optional.of(savedUser);
    }

    //FOR ADMIN ONLY
    public Optional<User> registerAdmin(RegisterRequestDTO userDto) {

        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        // THE DIFFERENCE: Fetch the ADMIN role
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: ADMIN role not found in database."));

        // Assign the Admin role
        user.getRoles().add(adminRole);

        User savedAdmin = userRepository.save(user);

        auditLogService.logAction(
                savedAdmin,
                AuditAction.ADMIN_REGISTERED,
                "New user registered with username: " + savedAdmin.getUsername()
        );

        return Optional.of(savedAdmin);
    }

    public UserDetails loadUserByUsername(String usernameOrEmail) {
        // Your existing logic to fetch the user from the repository...
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }



    //LOGIN
    public LoginResponseDTO login(LoginRequestDTO request){
        //validate username and password
        //AuthenticationManager handling credentials to AuthenticatorProvider
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

        var userDetails =
                loadUserByUsername(
                        request.getUsernameOrEmail()
                );

        //generate jwt token
        var jwtToken =
                jwtService.generateToken(userDetails);
        revokeAllUserToken(user);
        saveUserToken(jwtToken, user);

        //save audit-log
        auditLogService.logAction(
                user,
                AuditAction.USER_LOGIN,
                "User logged in successfully"
        );

        //provide response
        return new LoginResponseDTO(
                jwtToken,
                "Login Successful"
        );

    }

    private void saveUserToken(String jwtToken, User user) {
        var token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserToken(User user){
        List<Token> validToken = tokenRepository.getAllValidTokensByUser(user.getUserId());

        if(validToken.isEmpty()){
            return;
        }

        validToken.forEach(token -> {
                    token.setRevoked(true);
                    token.setExpired(true);
                }
        );

        tokenRepository.saveAll(validToken);

    }
}