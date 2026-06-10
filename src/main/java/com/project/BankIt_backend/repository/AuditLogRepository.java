package com.project.BankIt_backend.repository;

import com.project.BankIt_backend.entity.AuditLog;
import com.project.BankIt_backend.entity.Beneficiary;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAuditLogsByUser_UserId(Long userUserId);

    List<AuditLog> findAuditLogsByAction(AuditAction action);

    List<AuditLog> findTop10ByOrderByTimeStampDesc();

    List<AuditLog> findByTimeStampBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findByUser_UserIdAndAction(Long userId, AuditAction action);

}
