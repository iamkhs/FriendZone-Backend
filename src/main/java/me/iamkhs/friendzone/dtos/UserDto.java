package me.iamkhs.friendzone.dtos;

public record UserDto(
        Long id,
        String username,
        String email,
        String profilePic,
        String coverPic,
        String createdAt,
        boolean isEnable
) {

}
