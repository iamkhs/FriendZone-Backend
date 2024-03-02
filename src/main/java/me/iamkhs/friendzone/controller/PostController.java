package me.iamkhs.friendzone.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.PostDto;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.exceptions.UserNotMatchException;
import me.iamkhs.friendzone.request.LikePostRequest;
import me.iamkhs.friendzone.request.PostRequest;
import me.iamkhs.friendzone.service.CloudinaryService;
import me.iamkhs.friendzone.service.PostService;
import me.iamkhs.friendzone.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend-zone/post")
@CrossOrigin("*")
public class PostController {

    private final PostService postService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<PostDto> getFriendsPost(@Argument Long userId, Principal principal){
        log.info("inside getFriendsPost() method");
        String principalName = principal.getName();
        UserDto currentLoggedUser = userService.getUserByUsername(principalName);
        if (!currentLoggedUser.id().equals(userId)){
            throw new UserNotMatchException("User not match with logged user!");
        }
        return this.postService.findAllFriendsPost(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<PostDto> getPostByUsername(@Argument String username){
        log.info("inside getPostByUsername() method");
        return this.postService.getAllPostByUsername(username);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public PostDto getPostById(@Argument Long postId){
        log.info("inside getPostById() method");
        return this.postService.getPostById(postId);
    }


    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public Set<UserDto> getPostLikedUsers(@Argument Long postId){
        log.info("inside getPostLikedUsers() method");
        return this.postService.getPostLikedUsers(postId);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid MultipartHttpServletRequest request, Principal principal){
        log.info("inside createPost() method");
        long userId = Long.parseLong(request.getParameter("userId"));
        String postDetails = request.getParameter("details");
        MultipartFile file = request.getFile("image");

        String principalName = principal.getName();
        UserDto user = this.userService.getUserByUsername(principalName);
        if (!user.id().equals(userId)){
            throw new UserNotMatchException("User not Match with logged user!");
        }

        String imageUrl = null;
        if (file != null){
            imageUrl = this.cloudinaryService.uploadImage(file);
        }
        PostRequest post = new PostRequest(postDetails, userId, imageUrl);
        PostRequest postCreated = this.postService.createPost(post);
        log.info("post created successfully {}", postCreated);
        return ResponseEntity.ok(postCreated);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePostById(@PathVariable Long postId, Principal principal){
        log.info("inside deletePostById() method");
        this.postService.deletePost(postId, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/like-post")
    public ResponseEntity<?> likePost(@RequestBody LikePostRequest likePostRequest){
        log.info("inside likePost() method");
        this.postService.likePost(likePostRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/dislike-post")
    public ResponseEntity<?> dislikePost(@RequestBody LikePostRequest likePostRequest){
        log.info("inside dislikePost() method");
        this.postService.dislikePost(likePostRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-post/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody PostRequest newPost, Principal principal){
        log.info("inside updatePost() method");
        PostDto postDto = this.postService.updatePost(postId, newPost, principal.getName());
        log.info("post updated successfully {}", postDto);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

}