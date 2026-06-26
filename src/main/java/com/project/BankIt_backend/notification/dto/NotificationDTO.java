package com.project.BankIt_backend.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private String title;

    private String message;

    private String type;

    private LocalDateTime timestamp;

    private String senderUsername;

    private Long TransactionId;

}