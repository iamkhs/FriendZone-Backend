package me.iamkhs.friendzone.request;

public record LikePostRequest(
        Long userId,
        Long postId
) {
}
