package com.speako.domain.achievement.repository;

import com.speako.domain.achievement.domain.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
}
