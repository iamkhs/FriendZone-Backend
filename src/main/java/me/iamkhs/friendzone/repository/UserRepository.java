package me.iamkhs.friendzone.repository;

import me.iamkhs.friendzone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Set<User> findAllByIdNotIn(Set<Long> friendsId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Set<User> findAllByUsername(String username);
    Optional<User> findByVerificationCode(String code);
}
