package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.UpdateUserDTO;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    }

    public void deactivateUser(Long id){
        User user = getUserById(id);

        user.setStatus("NOT_ACTIVE");
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
}
