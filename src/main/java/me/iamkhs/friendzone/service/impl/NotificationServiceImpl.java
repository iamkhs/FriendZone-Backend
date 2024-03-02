package me.iamkhs.friendzone.service.impl;

import lombok.RequiredArgsConstructor;
import me.iamkhs.friendzone.dtos.NotificationDto;
import me.iamkhs.friendzone.entities.Notification;
import me.iamkhs.friendzone.repository.NotificationRepository;
import me.iamkhs.friendzone.service.NotificationService;
import me.iamkhs.friendzone.util.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(Notification notification) {
        this.notificationRepository.save(notification);
    }

    @Override
    public Long getRecentUnreadNotificationCount(Long receiverId) {
        return this.notificationRepository.countAllByReceiverIdAndIsRead(receiverId, false);
    }

    public List<NotificationDto> getAllNotification(Long receiverId){
        List<Notification> recentUnreadNotification = this.notificationRepository.findAllByReceiverIdOrderByNotificationTimeDesc(receiverId);
        return recentUnreadNotification
                .stream()
                .map(ModelMapper::notificationToDto)
                .toList();
    }

    @Override
    public void readAllUnreadNotification(Long userId) {
        List<Notification> notReadNotification = this.notificationRepository.findAllByReceiverIdAndIsRead(userId, false);
        notReadNotification.forEach(notification -> {
            System.err.println(notification.getNotificationContent());
            notification.setRead(true);
        });
        this.notificationRepository.saveAll(notReadNotification);
    }
}
