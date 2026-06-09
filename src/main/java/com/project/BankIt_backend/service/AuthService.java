package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.RegisterRequestDTO;
import com.project.BankIt_backend.entity.Role;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.repository.RoleRepository;
import com.project.BankIt_backend.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    
    //FOR REGULAR USER
    public Optional<User> registerUser(RegisterRequestDTO userDto) {

        // Initializing a blank User Entity object
        User user = new User();

        // Mapping data from DTO to the Entity
        user.setFullName(userDto.getFullName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        // Securely setting the password using the PasswordEncoder bean
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

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

        User savedUser = userRepository.save(user);

        return Optional.of(savedUser);
    }

    public UserDetails loadUserByUsername(String usernameOrEmail) {
        // Your existing logic to fetch the user from the repository...
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}