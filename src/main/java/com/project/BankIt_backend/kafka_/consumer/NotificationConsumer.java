package com.project.BankIt_backend.kafka_.consumer;

import com.project.BankIt_backend.common.enums.NotificationType;
import com.project.BankIt_backend.kafka_.event.TransactionCompletedEvent;
import com.project.BankIt_backend.notification.NotificationRepository;
import com.project.BankIt_backend.notification.NotificationService;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.project.BankIt_backend.kafka_.config.KafkaTopicConfig.TRANSACTION_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final UserService userService;

    @KafkaListener(
            topics = TRANSACTION_TOPIC,
            groupId = "notification-group"
    )
    public void consume(TransactionCompletedEvent transactionCompletedEvent){

        log.info("Notification Sent for {}", transactionCompletedEvent.getTransactionId());

        notificationService.createNotification(
                transactionCompletedEvent.getSenderId(),
                transactionCompletedEvent.getReceiverId(),
                transactionCompletedEvent.getTitle(),
                transactionCompletedEvent.getNotificationMessage(),
                transactionCompletedEvent.getNotificationType()
        );
    }
}
