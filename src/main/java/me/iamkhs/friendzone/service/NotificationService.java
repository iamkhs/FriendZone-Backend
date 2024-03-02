package me.iamkhs.friendzone.service;

import me.iamkhs.friendzone.dtos.NotificationDto;
import me.iamkhs.friendzone.entities.Notification;

import java.util.List;

public interface NotificationService {
    void sendNotification(Notification notification);
    void readAllUnreadNotification(Long userId);
    Long getRecentUnreadNotificationCount(Long receiverId);
    List<NotificationDto> getAllNotification(Long receiverId);
}
