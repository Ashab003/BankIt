package com.project.BankIt_backend.notification;

import com.project.BankIt_backend.transaction.Transaction;
import com.project.BankIt_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> getAllByReceiverOrderByCreatedAtDesc(User receiver);

    Notification getNotificationByNotificationId(Long notificationId);

    List<Notification> findByReceiverAndIsReadFalse(User receiver);

    long countNotificationByReceiver(User receiver);

    int countByReceiverAndIsReadFalse(User receiver);
}
