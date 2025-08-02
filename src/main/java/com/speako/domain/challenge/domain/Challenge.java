package com.speako.domain.challenge.domain;

import com.speako.domain.challenge.domain.enums.ChallengeLevel;
import com.speako.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, updatable = false, length = 20)
    private String title;

    @Column(nullable = false, updatable = false, length = 30)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_level", nullable = false)
    private ChallengeLevel challengeLevel;

    @Column(name = "goal_count", nullable = false, updatable = false)
    private int goalCount;

    @Column(name = "current_count", nullable = false)
    private int currentCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
