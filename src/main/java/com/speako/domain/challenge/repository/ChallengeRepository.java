package com.speako.domain.challenge.repository;

import com.speako.domain.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByNameAndLevel(String name, Integer level);
}
