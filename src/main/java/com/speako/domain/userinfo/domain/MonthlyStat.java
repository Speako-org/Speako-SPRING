package com.speako.domain.userinfo.domain;

import com.speako.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthlyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private int year;

    @Column(nullable = false, updatable = false)
    private int month;

    @Column(name = "avg_positive_ratio", nullable = false)
    private float avgPositiveRatio;

    @Column(name = "avg_negative_ratio", nullable = false)
    private float avgNegativeRatio;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;

    @Column(name = "max_streak", nullable = false)
    private int maxStreak;

    @Column(name = "last_streak_update_date")
    private LocalDate lastStreakUpdateDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 긍정/부정 비율 update
    public void updateRatios(float newPositiveRatio, float newNegativeRatio) {

        this.avgPositiveRatio = (this.avgPositiveRatio + newPositiveRatio) / 2;
        this.avgNegativeRatio = (this.avgNegativeRatio + newNegativeRatio) / 2;
    }

    // 현재 달의 연속 기록 기록일 수 update (maxStreak랑 비교 및 업데이트 포함)
    public void addCurrentStreak() {

        this.currentStreak += 1;
        if (this.currentStreak > this.maxStreak) {
            this.maxStreak = this.currentStreak;
        }
    }

    // currentStreak init
    public void initCurrentStreak() {
        this.currentStreak = 1;
    }

    // lastStreakUpdateDate update
    public void updateLastStreakUpdateDate(LocalDate today) {
        this.lastStreakUpdateDate = today;
    }
}
