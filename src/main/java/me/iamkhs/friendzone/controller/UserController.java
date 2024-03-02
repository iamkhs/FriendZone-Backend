package me.iamkhs.friendzone.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.User;
import me.iamkhs.friendzone.service.CloudinaryService;
import me.iamkhs.friendzone.service.UserService;
import me.iamkhs.friendzone.service.impl.EmailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend-zone/user")
@CrossOrigin("*")
public class UserController {

    @Value("${front-end.url}")
    private String url;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<UserDto> getAllUsers(@Argument Long userId){
        log.info("inside getAllUsers() method");
        return this.userService.getAllUsers(userId);
    }

    /**
     *  This api for get the user by its username
     */
    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public UserDto getUserByUsername(@Argument String username){
        log.info("inside getUserByUsername() method");
        return this.userService.getUserByUsername(username);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid MultipartHttpServletRequest request){
        log.info("inside createUser() method");
        MultipartFile file = request.getFile("image");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        String encodedPassword = passwordEncoder.encode(password);

        User user = buildUser(firstName, lastName, username, encodedPassword, email);

        if (file != null){
            String imageUrl = uploadImage(file);
            if (imageUrl == null) {
                return new ResponseEntity<>("Invalid Image format!", HttpStatus.BAD_REQUEST);
            }
            user.setProfilePic(imageUrl);
        }

        UserDto savedUser = this.userService.createUser(user);
        Thread sendVerificationCodeMail = new Thread(()->
                this.emailSenderService.sendMail(user, request));
        sendVerificationCodeMail.start();

        executorService.schedule(()->{
            // if the user still not verify his / her account in 5 minute then delete the user from database
            UserDto currentUser = userService.getUserByUsername(savedUser.username());
            if (!currentUser.isEnable()){
                userService.deleteUser(savedUser.id());
                log.info("User deleted for not verify his account within 5 minutes");
            }
        }, 5, TimeUnit.MINUTES);

        log.info("user created successfully {}", savedUser);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/signup/verify")
    public void verifyUser(@RequestParam String code, HttpServletResponse response) throws IOException {
        log.info("inside verifyUser() method");
        boolean verified = this.userService.isUserVerified(code);
        if (verified){
            response.sendRedirect(url+"/login");
            log.info("User verified Successfully, redirecting to login page");
        }else {
            response.sendRedirect(url+"/signup");
            log.error("User verification Field! redirecting to signup page");
        }
    }

    private static User buildUser(String firstName,
                                  String lastName,
                                  String username,
                                  String password,
                                  String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }


    @PostMapping("/profile-pic-upload/{username}")
    public ResponseEntity<?> setProfilePic(@PathVariable String username, @RequestParam("profilePic") MultipartFile file){
        log.info("inside setProfilePic() method");
        String profilePicUrl = uploadImage(file);
        if (profilePicUrl == null) {
            return new ResponseEntity<>("Invalid Image Format!!", HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = this.userService.setProfilePic(username, profilePicUrl);
        log.info("profile pic uploaded {}", userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/cover-pic-upload/{username}")
    public ResponseEntity<?> setCoverPic(@PathVariable String username, @RequestParam("coverPic") MultipartFile file){
        log.info("inside setCoverPic() method");
        String coverPicUrl = uploadImage(file);
        if (coverPicUrl == null) {
            return new ResponseEntity<>("Invalid Image Format!!", HttpStatus.BAD_REQUEST);
        }
        UserDto userDto = this.userService.setCoverPic(username, coverPicUrl);
        log.info("cover pic uploaded {}", userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/current-logged-user")
    public ResponseEntity<?> getCurrentLoggedUser(Principal principal){
        log.info("inside getCurrentLoggedUser() method");
        UserDto user = this.userService.getUserByUsername(principal.getName());
        log.info("current loggedUser {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private String uploadImage(MultipartFile file){
        return this.cloudinaryService.uploadImage(file);
    }
}
