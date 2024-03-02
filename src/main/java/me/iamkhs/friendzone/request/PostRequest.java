package me.iamkhs.friendzone.request;

public record PostRequest(
        String details,
        Long userId,
        String imageUrl
) {
}
