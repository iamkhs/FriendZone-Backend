package me.iamkhs.friendzone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.*;
import me.iamkhs.friendzone.repository.FriendshipRepository;
import me.iamkhs.friendzone.repository.UserRepository;
import me.iamkhs.friendzone.request.FriendRequest;
import me.iamkhs.friendzone.service.FriendshipService;
import me.iamkhs.friendzone.service.NotificationService;
import me.iamkhs.friendzone.util.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static me.iamkhs.friendzone.service.impl.UserServiceImpl.getUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public void sendFriendRequest(FriendRequest friendRequest) {
        Friendship friendship = this.friendshipRepository.findByUserIdAndFriendId(friendRequest.friendId(), friendRequest.userId());
        if (friendship == null) {
            User user = getUser(friendRequest.userId());
            User friend = getUser(friendRequest.friendId());
            Friendship sendRequest = new Friendship();
            sendRequest.setUser(friend);
            sendRequest.setFriend(user);
            sendRequest.setStatus(FriendshipStatus.PENDING);
            this.friendshipRepository.save(sendRequest);
            sendFriendRequestNotification(user, friend, true);
        }
    }

    @Override
    public void acceptFriendRequest(FriendRequest friendRequest) {
        Friendship friendship = this.friendshipRepository.findByUserIdAndFriendId(friendRequest.userId(), friendRequest.friendId());
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        Friendship anotherFriendship = new Friendship();
        anotherFriendship.setUser(friendship.getFriend());
        anotherFriendship.setFriend(friendship.getUser());
        anotherFriendship.setStatus(FriendshipStatus.ACCEPTED);

        this.friendshipRepository.save(friendship);
        this.friendshipRepository.save(anotherFriendship);
        this.sendFriendRequestNotification(friendship.getUser(), friendship.getFriend(), false);
    }

    void sendFriendRequestNotification(User user, User friend, boolean isSend){
        String notificationContent;
        if (isSend){
            notificationContent = "<b>" + user.getUsername() + "</b> send you a friend request";
        }else{
            notificationContent = "<b>" + user.getUsername() + "</b> accepted your friend request";
        }
        Notification notification = new Notification();
        notification.setNotificationContent(notificationContent);
        notification.setNotificationTime(LocalDateTime.now());
        notification.setSender(user);
        notification.setReceiver(friend);
        notification.setNotificationType(NotificationType.FRIENDREQUEST);
        if (!user.getId().equals(friend.getId())){
            this.notificationService.sendNotification(notification);
            log.info("Notification Send Successfully.");
        }
    }

    @Override
    public void declineFriendRequest(FriendRequest friendRequest) {
        Friendship friendship = this.friendshipRepository.findByUserIdAndFriendId(friendRequest.userId(), friendRequest.friendId());
        System.err.println(friendship.getStatus());
        this.friendshipRepository.delete(friendship);
    }

    @Override
    public void unfriendRequest(FriendRequest friendRequest) {
        Friendship friendship = this.friendshipRepository.findByUserIdAndFriendId(friendRequest.userId(), friendRequest.friendId());
        System.err.println(friendship.getStatus());
        this.friendshipRepository.delete(friendship);
        friendship = this.friendshipRepository.findByUserIdAndFriendId(friendRequest.friendId(), friendRequest.userId());
        System.err.println(friendship.getStatus());
        this.friendshipRepository.delete(friendship);
    }

    @Override
    public List<UserDto> pendingFriends(Long userId) {
        List<Friendship> pendingFriends = this.friendshipRepository.
                findByUserIdAndStatus(userId, FriendshipStatus.PENDING);
        return getUserDto(pendingFriends);
    }

    @Override
    public List<UserDto> getAllFriendsOfUser(Long userId) {
        List<Friendship> userAllFriends = this.friendshipRepository.findByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED);
        List<UserDto> userFriendsList = new ArrayList<>();
        userAllFriends.forEach(user-> userFriendsList.add(ModelMapper.userToUserDto(user.getFriend())));
        return userFriendsList;
    }

    private User getUser(Long id){
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) return user.get();
        throw new RuntimeException("User not Found!");
    }

    @Override
    public FriendshipStatus getUserFriendshipStatus(Long userId, Long friendId) {
        Friendship friendship = this.friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (friendship == null){
            friendship = this.friendshipRepository.findByUserIdAndFriendId(friendId, userId);
            if (friendship == null){
                return FriendshipStatus.NONE;
            }
        }
        return friendship.getStatus();
    }
}
