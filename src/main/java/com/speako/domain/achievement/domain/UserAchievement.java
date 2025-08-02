package com.speako.domain.achievement.domain;

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
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "self_comment", nullable = false, length = 50)
    private String selfComment;

    @Column(name = "total_recorded_days", nullable = false)
    private int totalRecordedDays;

    @Column(name = "avg_positive_ratio", nullable = false)
    private float avgPositiveRatio;

    @Column(name = "current_badge_count", nullable = false)
    private int currentBadgeCount;

    @Column(name = "total_badge_count", nullable = false)
    private int totalBadgeCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
