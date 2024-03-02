package me.iamkhs.friendzone.service;

import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.FriendshipStatus;
import me.iamkhs.friendzone.request.FriendRequest;

import java.util.List;

public interface FriendshipService {
    void sendFriendRequest(FriendRequest friendRequest);
    void acceptFriendRequest(FriendRequest friendRequest);
    void declineFriendRequest(FriendRequest friendRequest);
    void unfriendRequest(FriendRequest friendRequest);
    List<UserDto> pendingFriends(Long userId);
    List<UserDto> getAllFriendsOfUser(Long username);
    FriendshipStatus getUserFriendshipStatus(Long userId, Long friendId);
}
