package me.iamkhs.friendzone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.FriendshipStatus;
import me.iamkhs.friendzone.request.FriendRequest;
import me.iamkhs.friendzone.service.FriendshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend-zone/friendship")
@CrossOrigin("*")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/send-request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequest friendRequest){
        log.info("inside sendFriendRequest() method");
        this.friendshipService.sendFriendRequest(friendRequest);
        log.info("friend Request Send Successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/accept-request")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequest friendRequest){
        log.info("inside acceptFriendRequest() method");
        this.friendshipService.acceptFriendRequest(friendRequest);
        log.info("friend Request Accept Successfully");
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/decline-request")
    public ResponseEntity<?> declineFriendRequest(@RequestBody FriendRequest friendRequest){
        log.info("inside declineFriendRequest() method");
        this.friendshipService.declineFriendRequest(friendRequest);
        log.info("friend Request Decline Successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/unfriend")
    public ResponseEntity<?> unfriendRequest(@RequestBody FriendRequest friendRequest){
        log.info("inside unfriendRequest() method");
        this.friendshipService.unfriendRequest(friendRequest);
        log.info("unfriend Successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/pending-friends/{userId}")
    public ResponseEntity<?> pendingFriendsByUser(@PathVariable Long userId){
        log.info("inside pendingFriendsByUser() method");
        List<UserDto> pendingFriends = this.friendshipService.pendingFriends(userId);
        log.info("pending friends by user {}", pendingFriends);
        return ResponseEntity.ok(pendingFriends);
    }

    @GetMapping("/all-friends/{userId}")
    public ResponseEntity<?> getFriendsOfUser(@PathVariable Long userId){
        log.info("inside getFriendsOfUser() method");
        List<UserDto> userFriendsUsername = this.friendshipService.getAllFriendsOfUser(userId);
        log.info("friends of user {}", userFriendsUsername);
        return new ResponseEntity<>(userFriendsUsername, HttpStatus.OK);
    }

    @GetMapping("/status/{userId}/{friendId}")
    public ResponseEntity<?> getUserFriendshipStatus(@PathVariable Long userId, @PathVariable Long friendId){
        log.info("inside getUserFriendshipStatus() method");
        FriendshipStatus userFriendshipStatus = this.friendshipService.getUserFriendshipStatus(userId, friendId);
        log.info("userFriendshipStatus {}", userFriendshipStatus);
        return new ResponseEntity<>(userFriendshipStatus, HttpStatus.OK);
    }
}
