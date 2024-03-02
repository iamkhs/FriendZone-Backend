package me.iamkhs.friendzone.service.impl;

import lombok.RequiredArgsConstructor;
import me.iamkhs.friendzone.dtos.CommentDto;
import me.iamkhs.friendzone.entities.*;
import me.iamkhs.friendzone.exceptions.CommentNotFoundException;
import me.iamkhs.friendzone.exceptions.PostNotFoundException;
import me.iamkhs.friendzone.exceptions.UserNotFoundException;
import me.iamkhs.friendzone.repository.CommentRepository;
import me.iamkhs.friendzone.repository.PostRepository;
import me.iamkhs.friendzone.repository.UserRepository;
import me.iamkhs.friendzone.request.CommentRequest;
import me.iamkhs.friendzone.service.CommentService;
import me.iamkhs.friendzone.service.NotificationService;
import me.iamkhs.friendzone.util.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public Comment addComment(Long postId, CommentRequest commentRequest) {
        Optional<Post> optionalPost = this.postRepository.findById(postId);
        if (optionalPost.isEmpty()){
            throw new PostNotFoundException("Post Not Found!");
        }

        Optional<User> optionalUser = this.userRepository.findById(commentRequest.userId());
        if (optionalUser.isEmpty()){
            throw new UserNotFoundException("User not Found!");
        }

        User user = optionalUser.get(); // notification sender
        Post post = optionalPost.get();
        Comment comment = new Comment();
        comment.setBody(commentRequest.body());
        comment.setCommentAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setPost(post);

        User postAuthor = post.getUser(); // notification receiver
        this.postRepository.save(post);
        Comment savedComment = this.commentRepository.save(comment);
        if (!user.getId().equals(postAuthor.getId())) {
            sendCommentNotification(user, postAuthor, postId, commentRequest.body());
        }
        return savedComment;
    }

    private void sendCommentNotification(User user, User postAuthor, Long contentId, String commentBody) {
        String notificationContent = "<b>" + user.getUsername() + "</b> commented on your post: " + "<b>"+commentBody+"</b>";
        Notification notification = new Notification();
        notification.setNotificationContent(notificationContent);
        notification.setSender(user);
        notification.setReceiver(postAuthor);
        notification.setContentId(contentId);
        notification.setNotificationType(NotificationType.COMMENT);
        notification.setNotificationTime(LocalDateTime.now());
        this.notificationService.sendNotification(notification);
        System.err.println("Notification sent successfully");
    }


    @Override
    public void updateComment(Integer commentId, CommentRequest newComment) {
        Comment comment = this.commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        comment.setBody(newComment.body());
        this.commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Comment comment = this.commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        this.commentRepository.delete(comment);
    }

    @Override
    public CommentDto getCommentById(Integer commentId) {
        Comment comment = this.commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        return ModelMapper.commentToCommentDto(comment);
    }
}
