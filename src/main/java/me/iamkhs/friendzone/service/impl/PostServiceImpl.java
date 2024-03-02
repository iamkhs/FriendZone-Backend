package me.iamkhs.friendzone.service.impl;

import lombok.RequiredArgsConstructor;
import me.iamkhs.friendzone.dtos.PostDto;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.*;
import me.iamkhs.friendzone.exceptions.PostNotFoundException;
import me.iamkhs.friendzone.exceptions.UserNotFoundException;
import me.iamkhs.friendzone.exceptions.UserNotMatchException;
import me.iamkhs.friendzone.repository.PostRepository;
import me.iamkhs.friendzone.repository.UserRepository;
import me.iamkhs.friendzone.request.LikePostRequest;
import me.iamkhs.friendzone.request.PostRequest;
import me.iamkhs.friendzone.service.NotificationService;
import me.iamkhs.friendzone.service.PostService;
import me.iamkhs.friendzone.util.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public PostRequest createPost(PostRequest postRequest) {
        Long userId = postRequest.userId();
        User user = this.userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        Post post = new Post();
        post.setDetails(postRequest.details());
        post.setPostedAt(LocalDateTime.now());
        post.setUser(user);
        post.setImageUrl(postRequest.imageUrl());
        this.postRepository.save(post);
        return postRequest;
    }

    @Override
    public List<PostDto> findAllFriendsPost(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found!"));

        Set<Long> friendsIds = user.getFriendships()
                .stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.ACCEPTED))
                .map(friendship -> friendship.getFriend().getId())
                .collect(Collectors.toSet());
        friendsIds.add(user.getId()); // to getting all of current user also

        List<Post> postOfFriends = this.postRepository.findAllByUserIdIn(friendsIds);
        Collections.shuffle(postOfFriends);

        return postOfFriends.stream()
                .map(ModelMapper::postToPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllPostByUsername(String username){
        User user = this.userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        return this.postRepository.findAllByUserOrderByPostedAtDesc(user).stream()
                .map(ModelMapper::postToPostDto)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long id) {
        Optional<Post> post = this.postRepository.findById(id);
        if (post.isEmpty()){
            throw new PostNotFoundException("Post with this Id: "+ id +" not found!");
        }
        return ModelMapper.postToPostDto(post.get());
    }

    @Override
    public void deletePost(Long postId, String currentLoggedUser) {
        User user = this.userRepository.findByUsername(currentLoggedUser).orElseThrow(() -> new UserNotFoundException("User not Found!"));
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post Not Found!"));
        User postUser = post.getUser();
        if (postUser.equals(user)){
            this.postRepository.delete(post);
        }
        else throw new UserNotMatchException("User not Match!");
    }

    @Override
    public void likePost(LikePostRequest likePostRequest) {
        Post post = this.postRepository.findById(likePostRequest.postId()).orElseThrow(() ->
                new PostNotFoundException("Post Not found!!"));

        User postAuthor = post.getUser(); // receiver

        User user = this.userRepository.findById(likePostRequest.userId()).orElseThrow(() ->
                new UserNotFoundException("User not Found!!")); // sender

        if (!post.getPostLikes().contains(user)){
            post.getPostLikes().add(user);
            post.setTotalLikes(post.getTotalLikes()+1);
            this.postRepository.save(post);
            if (!postAuthor.getId().equals(user.getId())) {
                sendLikePostNotification(post, user, postAuthor);
            }
            System.err.println("like post");
        }
    }

    void sendLikePostNotification(Post post, User sender, User receiver){
        String notificationContent = "<b>" + sender.getUsername() + "</b> likes on your post: " + post.getDetails();
        Notification notification = new Notification();
        notification.setNotificationContent(notificationContent);
        notification.setNotificationType(NotificationType.LIKEPOST);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setNotificationTime(LocalDateTime.now());
        notification.setContentId(post.getId());
        this.notificationService.sendNotification(notification);
    }

    @Override
    public Set<UserDto> getPostLikedUsers(Long postId) {
        Post post = this.postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Post not found!"));

        return post.getPostLikes()
                .stream()
                .map(ModelMapper::userToUserDto)
                .collect(Collectors.toSet());
    }

    @Override
    public void dislikePost(LikePostRequest likePostRequest) {
        Post post = this.postRepository.findById(likePostRequest.postId()).orElseThrow(() ->
                new PostNotFoundException("Post Not found!!"));
        User user = this.userRepository.findById(likePostRequest.userId()).orElseThrow(() ->
                new UserNotFoundException("User not found!"));

        Set<User> postLikes = post.getPostLikes();
        postLikes.remove(user);
        if (post.getTotalLikes()>0){
            post.setTotalLikes(post.getTotalLikes()-1);
        }
        this.postRepository.save(post);
    }

    @Override
    public PostDto updatePost(Long postId, PostRequest newPost, String currentLoggedUser) {
        User currentUser = this.userRepository.findByUsername(currentLoggedUser).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post Not Found"));
        if (!post.getUser().equals(currentUser)){
            throw new UserNotMatchException("User not match!");
        }
        post.setDetails(newPost.details());
        post.setUpdateAt(LocalDateTime.now());
        Post updatedPost = this.postRepository.save(post);
        return ModelMapper.postToPostDto(updatedPost);
    }

    @Override
    public List<PostDto> getAllPostByQuery(String query) {
        List<Post> allPostByQuery = this.postRepository.findAllByDetailsContaining(query);
        return allPostByQuery.stream()
                .map(ModelMapper::postToPostDto).toList();
    }
}
