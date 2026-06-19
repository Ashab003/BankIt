package com.project.BankIt_backend.audit;

import com.project.BankIt_backend.audit.dto.AuditLogResponseDTO;
import com.project.BankIt_backend.common.enums.AuditAction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDTO>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                auditLogService.getLogsByUser(userId)
        );
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByAction(
            @PathVariable String action) {

        return ResponseEntity.ok(
                auditLogService.getLogsByAction(action)
        );
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLogResponseDTO>> getRecentLogs() {

        return ResponseEntity.ok(
                auditLogService.getRecentLogs()
        );
    }

    @GetMapping("/between")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsBetweenDates(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {

        return ResponseEntity.ok(
                auditLogService.getLogsBetweenDates(start, end)
        );
    }

    @GetMapping("/user/{userId}/action/{action}")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByUserAndAction(
            @PathVariable Long userId,
            @PathVariable AuditAction action) {

        return ResponseEntity.ok(
                auditLogService.getLogsByUserAndAction(
                        userId,
                        action
                )
        );
    }
}
