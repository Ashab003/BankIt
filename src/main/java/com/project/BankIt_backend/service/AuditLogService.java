package com.project.BankIt_backend.service;

import com.project.BankIt_backend.dto.AuditLogResponseDTO;
import com.project.BankIt_backend.entity.AuditLog;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import com.project.BankIt_backend.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void logAction(User user,
                          AuditAction action,
                          LocalDateTime localDateTime,
                          String description) {
        System.out.println("\n LOGOUT ACTION \n");

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setDescription(description);
        auditLog.setTimeStamp(localDateTime);

        AuditLog saved = auditLogRepository.save(auditLog);

        System.out.println("\nAUDIT ID = " + saved.getLogId() + "\n");
    }


    public List<AuditLogResponseDTO> getAllLogs(){

        return auditLogRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    public List<AuditLogResponseDTO> getLogsByUser(Long userId){

        return auditLogRepository.findAuditLogsByUser_UserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<AuditLogResponseDTO> getLogsByAction(String action){

        try{
            AuditAction auditAction =
                    AuditAction.valueOf(action.toUpperCase());

            return auditLogRepository
                    .findAuditLogsByAction(auditAction)
                    .stream()
                    .map(this::mapToDto)
                    .toList();

        } catch (IllegalArgumentException e){
            return Collections.emptyList();
        }
    }

    public List<AuditLogResponseDTO> getLogsBetweenDates(
            LocalDateTime start,
            LocalDateTime end){

        return auditLogRepository
                .findByTimeStampBetween(start,end)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<AuditLogResponseDTO> getRecentLogs(){

        return auditLogRepository
                .findTop10ByOrderByTimeStampDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<AuditLogResponseDTO> getLogsByUserAndAction(
            Long userId,
            AuditAction action){

        return auditLogRepository
                .findByUser_UserIdAndAction(userId, action)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private AuditLogResponseDTO mapToDto(AuditLog log){

        return new AuditLogResponseDTO(
                log.getLogId(),
                log.getUser().getUserId(),
                log.getUser().getUsername(),
                log.getAction(),
                log.getTimeStamp(),
                log.getDescription()
        );
    }
}
