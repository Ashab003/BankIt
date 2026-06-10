package com.project.BankIt_backend.dto;

import com.project.BankIt_backend.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long logId;

    private Long userId;

    private String username;

    private AuditAction action;

    private LocalDateTime timeStamp;

    private String description;
}