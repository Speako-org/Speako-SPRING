package com.speako.domain.challenge.repository;

import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUserAndIsActiveTrue(User user);

    List<UserChallenge> findByUserIdAndIsActiveTrue(Long userId);
}
