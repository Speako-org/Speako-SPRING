package com.speako.domain.challenge.repository;

import com.speako.domain.challenge.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
