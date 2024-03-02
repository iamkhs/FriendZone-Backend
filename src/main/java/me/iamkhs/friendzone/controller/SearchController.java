package me.iamkhs.friendzone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.PostDto;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.service.PostService;
import me.iamkhs.friendzone.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/friend-zone/search")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SearchController {
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/")
    public ResponseEntity<?> search(@RequestParam("query") String query, @RequestParam("type") String type){
        log.info("inside search() method search type {}", type);
        if (type.equals("users")) {
            Set<UserDto> allUsersByQuery = this.userService.getAllUsersByQuery(query);
            log.info("allUsersByQuery {}", allUsersByQuery);
            return new ResponseEntity<>(allUsersByQuery, HttpStatus.OK);
        }else{
            List<PostDto> allPostByQuery = this.postService.getAllPostByQuery(query);
            log.info("allPostByQuery {}", allPostByQuery);
            return new ResponseEntity<>(allPostByQuery, HttpStatus.OK);
        }
    }
}
