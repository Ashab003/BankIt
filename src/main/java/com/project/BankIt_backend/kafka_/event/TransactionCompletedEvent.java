package com.project.BankIt_backend.kafka_.event;

import com.project.BankIt_backend.common.enums.NotificationType;
import com.project.BankIt_backend.user.User;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCompletedEvent {

    private Long transactionId;

    private Long senderId;
    private Long receiverId;

    private String title;
    private String notificationMessage;

    private NotificationType notificationType;
}