package com.project.BankIt_backend.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterResponseDTO {

    private Long userId;
    private String fullName;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    
}
