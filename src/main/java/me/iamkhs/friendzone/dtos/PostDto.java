package me.iamkhs.friendzone.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public record PostDto(
        Long postId,
        String postDetails,
        LocalDateTime postedAt,
        LocalDateTime updatedAt,
        Set<CommentDto> comments,
        String username,
        String postImageUrl,
        String userProfileUrl,
        Set<String> postLikedUsers,
        int totalLikes
) {
}
