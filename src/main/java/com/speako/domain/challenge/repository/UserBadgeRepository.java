package com.speako.domain.challenge.repository;

import com.speako.domain.challenge.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
}
