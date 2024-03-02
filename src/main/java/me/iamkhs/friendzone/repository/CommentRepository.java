package me.iamkhs.friendzone.repository;

import me.iamkhs.friendzone.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
