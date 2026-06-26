package com.project.BankIt_backend.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    Long notificationId;

    String title;

    String message;

    String type;

    boolean isRead;

    LocalDateTime createdAt;
}
