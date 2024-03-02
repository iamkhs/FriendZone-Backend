package me.iamkhs.friendzone.request;

import me.iamkhs.friendzone.dtos.UserDto;

public record JwtResponse(
        String token,
        UserDto userDto
) {
}
