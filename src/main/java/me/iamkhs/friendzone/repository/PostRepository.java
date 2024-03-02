package me.iamkhs.friendzone.repository;

import me.iamkhs.friendzone.entities.Post;
import me.iamkhs.friendzone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserIdIn(Set<Long> usersId);
    List<Post> findAllByUserOrderByPostedAtDesc(User user);
    List<Post> findAllByDetailsContaining(String details);
}
