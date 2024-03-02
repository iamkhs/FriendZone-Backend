package me.iamkhs.friendzone.dtos;

import java.time.LocalDateTime;

public record CommentDto(
        Integer id,
        String body,
        UserDto commenter,
        LocalDateTime commentAt
) {
}
