package com.project.BankIt_backend.notification;

import com.project.BankIt_backend.common.enums.NotificationType;
import com.project.BankIt_backend.common.exception.dto.UnauthorizedAccessException;
import com.project.BankIt_backend.notification.dto.NotificationDTO;
import com.project.BankIt_backend.notification.dto.NotificationResponseDTO;
import com.project.BankIt_backend.user.User;
import com.project.BankIt_backend.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserService userService;


    public void sendMessage(String msg){
        simpMessagingTemplate.convertAndSend("/topic/alerts", msg);
    }

    public void sendPrivateMessageToUser(NotificationDTO dto, String username){
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/alerts", dto);
    }

    public void saveNotification(
            User sender,
            User receiver,
            String title,
            String message,
            NotificationType type
    ) {

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public void createNotification(
            User sender,
            User receiver,
            String title,
            String message,
            NotificationType type
    ) {

        saveNotification(sender, receiver, title, message, type);

        sendPrivateMessageToUser(
                NotificationDTO.builder()
                        .title(title)
                        .message(message)
                        .type(NotificationType.MONEY_RECEIVED.name())
                        .timestamp(LocalDateTime.now())
                        .build(),

                receiver.getUsername()
        );

    }

    public List<NotificationResponseDTO> loadAllNotification(){
        User currentUser = userService.getCurrentUser();

        //current user is the receiver
        List<Notification> notifications = notificationRepository.getAllByReceiverOrderByCreatedAtDesc(currentUser);

        return notifications.stream()
                .map(notification -> NotificationResponseDTO.builder()
                        .notificationId(notification.getNotificationId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType().name())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void updateReadStatus(Long notificationId) {

        User currentUser = userService.getCurrentUser();

        Notification notification = notificationRepository
                .getNotificationByNotificationId(notificationId);

        if (!notification.getReceiver().getUserId()
                .equals(currentUser.getUserId())) {
            throw new UnauthorizedAccessException("You are not Authorized to do this");
        }

        notification.setRead(true);
    }

    @Transactional
    public void updateAllReadStatus() {

        User currentUser = userService.getCurrentUser();

        List<Notification> notifications =
                notificationRepository.findByReceiverAndIsReadFalse(currentUser);

        notifications.forEach(notification -> notification.setRead(true));
    }
}
