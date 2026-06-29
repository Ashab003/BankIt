package com.project.BankIt_backend.auth;

import com.project.BankIt_backend.auth.dto.*;
import com.project.BankIt_backend.common.enums.OTPVerificationStatus;
import com.project.BankIt_backend.common.exception.*;
import com.project.BankIt_backend.email.EmailService;
import com.project.BankIt_backend.user.Role;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.common.enums.AuditAction;
import com.project.BankIt_backend.common.enums.TokenType;
import com.project.BankIt_backend.account.AccountRepository;
import com.project.BankIt_backend.user.RoleRepository;
import com.project.BankIt_backend.user.UserRepository;
import com.project.BankIt_backend.security.JwtService;
import com.project.BankIt_backend.account.AccountService;
import com.project.BankIt_backend.audit.AuditLogService;
import com.project.BankIt_backend.user.UserService;
import com.project.BankIt_backend.user.dto.ChangePasswordRequestDTO;
import com.project.BankIt_backend.user.dto.ChangePasswordResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;

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
                .orElseThrow(() -> new InvalidRole("Error: Role not found in database."));

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
                LocalDateTime.now(),
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
                LocalDateTime.now(),
                "New user registered with username: " + savedAdmin.getUsername()
        );

        return Optional.of(savedAdmin);
    }

    @Cacheable(
            value = "user_details",
            key = "#usernameOrEmail"
    )
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        //fetch the user from the repository
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
                LocalDateTime.now(),
                "User logged in successfully"
        );

        //provide response
        return new LoginResponseDTO(
                jwtToken,
                "Login Successful"
        );

    }


    @Transactional
    public ChangePasswordResponseDTO changePassword(
            ChangePasswordRequestDTO request) {

        //does not authenticate the user, user was authenticated earlier by your JWT filter
        Authentication authentication = //who is the current authenticated user
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        //Check current password
        if (!passwordEncoder.matches(       //matches and equals are different things, equals compares strings, matches compares hashed versions
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Current password is incorrect"
            );
        }

        //Check new password confirmation
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        //prevent reusing same password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        auditLogService.logAction(
                user,
                AuditAction.PASSWORD_CHANGED,
                LocalDateTime.now(),
                "User changed password successfully"
        );

        return new ChangePasswordResponseDTO(
                "Password updated successfully"
        );
    }

    public SendOtpResponseDTO sendOTP(SendOtpRequestDTO request) {

        String mail = request.getEmail();

        //checking if mail exists
        if(!userRepository.findByEmail(mail).isPresent()) {
            throw new MailNotFound("Entered email is incorrect");
        }

        //cooldown for resending OTP
        String cooldownKey = "forgot-password:cooldown:" + mail;

        Long timeLeft = stringRedisTemplate.getExpire(
                cooldownKey,
                TimeUnit.SECONDS
        );

        if (timeLeft != null && timeLeft > 0) {
            throw new OtpCooldownException(
                    "Please wait " + timeLeft +
                            " seconds before requesting another OTP."
            );
        }
        //generate otp
        String OTP = generateOTP();

        //store the otp in redis
        String key = "forgot-password:" + mail;
        stringRedisTemplate.opsForValue().set(
                key,
                OTP,
                5,
                TimeUnit.MINUTES
        );

        //storing cooldown keys
        stringRedisTemplate.opsForValue().set(
                cooldownKey,
                "ACTIVE",
                30,
                TimeUnit.SECONDS
        );

        //send mail for otp
        try {

            // Send OTP email
            emailService.sendMailForOtp(
                    mail,
                    OTP
            );

        } catch (Exception e) {

            //Removing OTP and cool down since the email was not delivered
            stringRedisTemplate.delete(key);
            stringRedisTemplate.delete(cooldownKey);

            throw new EmailSendingException(
                    "Unable to send OTP. Please try again."
            );
        }

        return new SendOtpResponseDTO(
                "If an account with this email exists, an OTP has been sent."
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

    //generates 4 digit otp
    public static String generateOTP() {
        int randomPin   =(int) (Math.random()*9000)+1000;
        String otp  = String.valueOf(randomPin);
        return otp;
    }

    //validation otp
    public VerifyOtpResponseDTO verifyOtp(VerifyOtpRequestDTO requestDTO) {

        String email = requestDTO.getEmail();
        String key = "forgot-password:" + email;

        String storedOtp = stringRedisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new OtpExpiredException(
                    "OTP has expired. Please request a new one."
            );
        }

        String enteredOtp = requestDTO.getOtp();

        //checking if otp stored in redis cache matches the entered otp
        if (!storedOtp.equals(enteredOtp)){
            return new VerifyOtpResponseDTO(
                    OTPVerificationStatus.INVALID
            );
        }

        //otp is matched so we return that it is valid, so we remove that otp from redis cache so it will not be used again
        stringRedisTemplate.delete(key);

        //since otp is matched a new session to change
        //password is generated so only when this session is there password can be changed
        String resetKey = "reset-session:" + email;
        stringRedisTemplate.opsForValue().set(
                resetKey, "VERIFIED", 5, TimeUnit.MINUTES )
        ;

        return new VerifyOtpResponseDTO(
                OTPVerificationStatus.VALID
        );
    }

    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO requestDTO) {

        //checking if otp is verified
        String resetKey = "reset-session:" + requestDTO.getEmail();

        if (stringRedisTemplate.opsForValue().get(resetKey) == null) {
            throw new InvalidOtpException("OTP verification required.");
        }

        //finding user through email
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new MailNotFound("Email not found"));

        //new password should be the new password
        if (passwordEncoder.matches(
                requestDTO.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        //updating password
        user.setPassword(
                passwordEncoder.encode(requestDTO.getNewPassword())
        );

        userRepository.save(user);

        //as the password is reset delete the reset password session
        stringRedisTemplate.delete(resetKey);

        //for safety removing the token of user
        revokeAllUserToken(user);

        //logging that password is changed
        auditLogService.logAction(
                user,
                AuditAction.PASSWORD_CHANGED,
                LocalDateTime.now(),
                "User changed password successfully"
        );

        return new ResetPasswordResponseDTO(
                "Password updated successfully"
        );
    }


}


