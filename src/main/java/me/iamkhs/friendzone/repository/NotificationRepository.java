package me.iamkhs.friendzone.repository;

import me.iamkhs.friendzone.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByReceiverIdOrderByNotificationTimeDesc(Long receiverId);
    List<Notification> findAllByReceiverIdAndIsRead(Long receiverId, boolean isRead);
    Long countAllByReceiverIdAndIsRead(Long receiverId, boolean isRead);
}
