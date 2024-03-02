package me.iamkhs.friendzone.repository;

import me.iamkhs.friendzone.entities.Friendship;
import me.iamkhs.friendzone.entities.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUserIdAndStatus(Long userId, FriendshipStatus status);
    List<Friendship> findByFriendIdAndStatus(Long friendId, FriendshipStatus status);
    Friendship findByUserIdAndFriendId(Long userId, Long friendId);
}
