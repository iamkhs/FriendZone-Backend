package me.iamkhs.friendzone.dtos;

import me.iamkhs.friendzone.entities.NotificationType;

import java.time.LocalDateTime;

public record NotificationDto(
        Integer id,
        String content,
        Long contentId,
        Long receiverId,
        Long senderId,
        LocalDateTime notificationTime,
        NotificationType notificationType,
        boolean isRead
) {
}
