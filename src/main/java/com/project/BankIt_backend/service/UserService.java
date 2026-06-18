package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.BeneficiarySearchResponseDTO;
import com.project.BankIt_backend.dto.FullNameResponseDTO;
import com.project.BankIt_backend.dto.UpdateUserDTO;
import com.project.BankIt_backend.entity.Account;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import com.project.BankIt_backend.repository.AccountRepository;
import com.project.BankIt_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final CacheManager cacheManager;

    public User getUserById(Long id){
        return userRepository.findByUserId(id).orElseThrow(
                () -> new RuntimeException("User not found by id: " + id)
        );
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                ()-> new RuntimeException(("User Not found with the Username: " + username))
        );
    }

    public void updateUser(Long id, UpdateUserDTO dto){

    }

    public void changePassword(Long id, String oldPassword, String newPassword){
        User user = getUserById(id);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);

        cacheManager.getCache("user_details")
                .evict(user.getUsername());

        auditLogService.logAction(
                user,
                AuditAction.PASSWORD_CHANGED,
                LocalDateTime.now(),
                "Password changed successfully"
        );
    }

    public void deactivateUser(Long id){
        User user = getUserById(id);

        user.setStatus("NOT_ACTIVE");
        userRepository.save(user);

        cacheManager.getCache("user_details")
                .evict(user.getUsername());
    }

    public List<User> getAllUsers(){
         return userRepository.findAll();
    }


    public User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public FullNameResponseDTO getUserFullName() {
        return new FullNameResponseDTO(
               getCurrentUser().getFullName()
        );
    }

    public User getUserByUsernameOrEmail(String usernameOrEmail) {

        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + usernameOrEmail
                        ));
    }

    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId
                        ));

        if ("DELETED".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException(
                    "User is already deleted"
            );
        }

        user.setStatus("DELETED");

        userRepository.save(user);

        auditLogService.logAction(
                user,
                AuditAction.PROFILE_UPDATED,
                LocalDateTime.now(),
                "User account marked as DELETED by admin"
        );
    }
}
