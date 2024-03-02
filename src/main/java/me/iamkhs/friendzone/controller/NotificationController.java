package me.iamkhs.friendzone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.NotificationDto;
import me.iamkhs.friendzone.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend-zone/notification")
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllNotifications(@PathVariable Long userId){
        log.info("inside getAllNotifications() method");
        List<NotificationDto> allNotification = this.notificationService.getAllNotification(userId);
        log.info("all notifications {}", allNotification);
        return new ResponseEntity<>(allNotification, HttpStatus.OK);
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getUnreadNotificationCount(@PathVariable Long userId){
        log.info("inside getUnreadNotificationCount() method");
        Long unreadNotificationCount = this.notificationService.getRecentUnreadNotificationCount(userId);
        return new ResponseEntity<>(unreadNotificationCount, HttpStatus.OK);
    }

    @PostMapping("/read/{userId}")
    public ResponseEntity<?> readAllUnreadNotification(@PathVariable Long userId){
        log.info("inside readAllUnreadNotification() method");
        this.notificationService.readAllUnreadNotification(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
