package com.project.BankIt_backend.notification;


import com.project.BankIt_backend.notification.dto.NotificationCountDTO;
import com.project.BankIt_backend.notification.dto.NotificationDTO;
import com.project.BankIt_backend.notification.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/all-notifications")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications(){

        notificationService.loadAllNotification();

        return ResponseEntity.ok(
                notificationService.loadAllNotification()
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId) {

        notificationService.updateReadStatus(notificationId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> readAllNotifications() {

        notificationService.updateAllReadStatus();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notification-count")
    public ResponseEntity<NotificationCountDTO> getNotificationCount(){
        return ResponseEntity.ok(notificationService.getUnreadNotificationCount());
    }
}
