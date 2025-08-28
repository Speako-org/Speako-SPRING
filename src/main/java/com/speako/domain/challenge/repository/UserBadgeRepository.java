package com.speako.domain.challenge.repository;

import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user.id = :userId AND ub.isMain = true")
    Optional<UserBadge> findByUserIdAndIsMain(@Param("userId") Long userId);

    @Query("SELECT ub FROM UserBadge ub WHERE ub.id = :userBadgeId AND ub.user.id = :userId")
    Optional<UserBadge> findByIdAndUserId(@Param("userBadgeId") Long userBadgeId, @Param("userId") Long userId);

    boolean existsByUserAndBadge(User user, Badge badge);

    List<UserBadge> findByUserId(Long userId);
}