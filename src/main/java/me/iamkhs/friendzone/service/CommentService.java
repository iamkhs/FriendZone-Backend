package me.iamkhs.friendzone.service;

import me.iamkhs.friendzone.dtos.CommentDto;
import me.iamkhs.friendzone.entities.Comment;
import me.iamkhs.friendzone.request.CommentRequest;

public interface CommentService {
    Comment addComment(Long postId, CommentRequest comment);
    void updateComment(Integer commentId, CommentRequest newComment);
    void deleteComment(Integer commentId);
    CommentDto getCommentById(Integer commentId);
}
