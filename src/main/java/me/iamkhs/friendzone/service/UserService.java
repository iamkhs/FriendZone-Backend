package me.iamkhs.friendzone.service;

import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto createUser(User user);
    List<UserDto> getAllUsers(Long userId);
    Set<UserDto> getAllUsersByQuery(String query);
    UserDto getUserByUsername(String username);
    UserDto setProfilePic(String username, String profilePicUrl);
    UserDto setCoverPic(String username, String coverPicUrl);

    boolean isUserVerified(String code);

    void deleteUser(Long userId);
}
