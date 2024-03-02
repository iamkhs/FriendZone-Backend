package me.iamkhs.friendzone.service;

import me.iamkhs.friendzone.dtos.PostDto;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.request.LikePostRequest;
import me.iamkhs.friendzone.request.PostRequest;

import java.util.List;
import java.util.Set;

public interface PostService {
    List<PostDto> findAllFriendsPost(Long userId);
    List<PostDto> getAllPostByUsername(String username);
    Set<UserDto> getPostLikedUsers(Long postId);
    PostDto getPostById(Long id);
    PostRequest createPost(PostRequest post);
    void deletePost(Long postId, String currentLoggedUser);
    void dislikePost(LikePostRequest likePostRequest);
    void likePost(LikePostRequest likePostRequest);
    PostDto updatePost(Long postId, PostRequest newPost, String currentLoggedUser);
    List<PostDto> getAllPostByQuery(String query);
}
