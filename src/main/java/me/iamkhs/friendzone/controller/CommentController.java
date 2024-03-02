package me.iamkhs.friendzone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.CommentDto;
import me.iamkhs.friendzone.entities.Comment;
import me.iamkhs.friendzone.request.CommentRequest;
import me.iamkhs.friendzone.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend-zone/post/comment")
@CrossOrigin("*")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest){
        log.info("inside addComment() method");
        Comment addedComment = this.commentService.addComment(postId, commentRequest);
        log.info("saved Comment {}", addedComment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable Integer commentId, @RequestBody CommentRequest newComment){
        log.info("inside editComment() method");
        this.commentService.updateComment(commentId, newComment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Integer commentId){
        log.info("inside getCommentById() method");
        CommentDto comment = this.commentService.getCommentById(commentId);
        log.info("getCommentById {}", comment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId){
        log.info("inside deleteComment() method");
        this.commentService.deleteComment(commentId);
        log.info("comment deleted successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
