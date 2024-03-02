package me.iamkhs.friendzone.util;

import me.iamkhs.friendzone.dtos.CommentDto;
import me.iamkhs.friendzone.dtos.NotificationDto;
import me.iamkhs.friendzone.dtos.PostDto;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.Comment;
import me.iamkhs.friendzone.entities.Notification;
import me.iamkhs.friendzone.entities.Post;
import me.iamkhs.friendzone.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelMapper {

    private ModelMapper(){}

    public static UserDto userToUserDto(User user){
        LocalDateTime createdAt = user.getCreatedAt();
        String formattedDate = createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return new UserDto(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePic(),
                user.getCoverPic(),
                formattedDate,
                user.isEnable());
    }

    public static PostDto postToPostDto(Post post){
        Set<CommentDto> comments = post.getComments().stream()
                .map(ModelMapper::commentToCommentDto)
                .collect(Collectors.toSet());

        Set<String> postLikedUsers = post.getPostLikes().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        return new PostDto(post.getId(),
                post.getDetails(),
                post.getPostedAt(),
                post.getUpdateAt(),
                comments.isEmpty() ? null : comments,
                post.getUser().getUsername(),
                post.getImageUrl(),
                post.getUser().getProfilePic(),
                postLikedUsers,
                post.getTotalLikes()
        );
    }

    public static CommentDto commentToCommentDto(Comment comment){
        return new CommentDto(
                comment.getId(),
                comment.getBody(),
                userToUserDto(comment.getUser()),
                comment.getCommentAt()
        );
    }

    public static NotificationDto notificationToDto(Notification notification){
        return new NotificationDto(notification.getId(),
                notification.getNotificationContent(),
                notification.getContentId(),
                notification.getReceiver().getId(),
                notification.getSender().getId(),
                notification.getNotificationTime(),
                notification.getNotificationType(),
                notification.isRead());
    }
}
