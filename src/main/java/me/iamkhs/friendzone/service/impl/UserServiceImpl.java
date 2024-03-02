package me.iamkhs.friendzone.service.impl;

import lombok.RequiredArgsConstructor;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.Friendship;
import me.iamkhs.friendzone.entities.FriendshipStatus;
import me.iamkhs.friendzone.entities.Post;
import me.iamkhs.friendzone.entities.User;
import me.iamkhs.friendzone.exceptions.EmailAlreadyRegisteredException;
import me.iamkhs.friendzone.exceptions.UserNotFoundException;
import me.iamkhs.friendzone.exceptions.UsernameAlreadyRegisteredException;
import me.iamkhs.friendzone.repository.FriendshipRepository;
import me.iamkhs.friendzone.repository.PostRepository;
import me.iamkhs.friendzone.repository.UserRepository;
import me.iamkhs.friendzone.service.UserService;
import me.iamkhs.friendzone.util.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PostRepository postRepository;

    @Override
    public UserDto createUser(User user) {
        Optional<User> userByEmail = this.userRepository.findByEmail(user.getEmail());
        Optional<User> userByUsername = this.userRepository.findByUsername(user.getUsername());
        if (userByEmail.isPresent()){
            throw new EmailAlreadyRegisteredException("This email is already Registered!");
        }
        if (userByUsername.isPresent()){
            throw new UsernameAlreadyRegisteredException("This username is already Registered!");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setVerificationCode(generateVerificationCode());
        this.userRepository.save(user);
        return ModelMapper.userToUserDto(user);
    }

    private String generateVerificationCode(){
        return UUID.randomUUID().toString();
    }

    static List<UserDto> getUserDto(List<Friendship> friends) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (var friendship : friends){
            User friend = friendship.getFriend();
            UserDto userDto = ModelMapper.userToUserDto(friend);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public List<UserDto> getAllUsers(Long userId) {
        User currentUser = this.userRepository.findById(userId).orElseThrow(()->
                new RuntimeException("User Not Found"));

        Set<Friendship> userFriends = currentUser.getFriendships();
        List<Friendship> pendingFriends = this.friendshipRepository.findByUserIdAndStatus(currentUser.getId(), FriendshipStatus.PENDING);
        userFriends.addAll(pendingFriends);

        Set<Long> friendsIds = userFriends.stream()
                .map(friend -> friend.getFriend().getId())
                .collect(Collectors.toSet());
        friendsIds.add(currentUser.getId());

        pendingFriends = this.friendshipRepository.findByFriendIdAndStatus(currentUser.getId(), FriendshipStatus.PENDING);
        pendingFriends.forEach(friend-> friendsIds.add(friend.getUser().getId()));

        List<UserDto> allUsers = this.userRepository.findAllByIdNotIn(friendsIds).stream()
                .map(ModelMapper::userToUserDto)
                .distinct()
                .collect(Collectors.toList());
        Collections.shuffle(allUsers);
        return allUsers;
    }

    @Override
    public boolean isUserVerified(String verificationCode) {
        LocalDateTime currDate = LocalDateTime.now();
        Optional<User> optionalUser = this.userRepository.findByVerificationCode(verificationCode);
        if (optionalUser.isEmpty()){
            return false;
        }

        User user = optionalUser.get();
        LocalDateTime userRegistrationDate = user.getCreatedAt();
        long minute = Duration.between(userRegistrationDate, currDate).toMinutes();
        System.err.println(minute);
        if (minute <= 5){
            user.setVerificationCode(null);
            user.setEnable(true);
            this.userRepository.save(user);
            return true;
        }
        this.userRepository.delete(user);
        return false;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with this username not found!"));
        return ModelMapper.userToUserDto(user);
    }

    @Override
    public UserDto setProfilePic(String username, String profilePicUrl) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User Not found!"));

        user.setProfilePic(profilePicUrl);
        Post post = new Post();
        post.setImageUrl(profilePicUrl);
        post.setPostedAt(LocalDateTime.now());
        post.setDetails("");
        post.setUser(user);

        User updatedUser = this.userRepository.save(user);
        this.postRepository.save(post);

        return ModelMapper.userToUserDto(updatedUser);
    }

    @Override
    public UserDto setCoverPic(String username, String coverPicUrl) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User Not found!"));
        user.setCoverPic(coverPicUrl);
        User updatedUser = this.userRepository.save(user);
        return ModelMapper.userToUserDto(updatedUser);
    }

    @Override
    public Set<UserDto> getAllUsersByQuery(String query) {
        Set<User> allByUsername = this.userRepository.findAllByUsername(query);
        return allByUsername.stream().map(ModelMapper::userToUserDto).collect(Collectors.toSet());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found!!"));
        this.userRepository.delete(user);
    }
}
