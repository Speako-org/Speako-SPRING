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
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    private User user;

    @Column(name = "self_comment", nullable = false, length = 50)
    private String selfComment;

    @Column(name = "total_record_count", nullable = false)
    private int totalRecordCount;

    @Column(name = "total_recorded_days", nullable = false)
    private int totalRecordedDays;

    @Column(name = "last_recorded_date")
    // totalRecordedDays를 가장 최근에 갱신시킨 기록(해당 일의 첫 기록)의 createAt
    private LocalDate lastRecordedDate;

    @Column(name = "avg_positive_ratio", nullable = false)
    // 사용자의 기록별 평균 긍정표현 사용률
    private float avgPositiveRatio;

    @Column(name = "current_badge_count", nullable = false)
    private int currentBadgeCount;

    @Column(name = "total_badge_count", nullable = false)
    private int totalBadgeCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateSelfComment(String newSelfComment) {
        this.selfComment = newSelfComment;
    }

    // 기록 추가에 따른 UserAchievement 업데이트
    public void addRecord(float newPositiveRatio, LocalDateTime createdAt) {

        // 전체 기록 수 증가
        this.totalRecordCount++;

        // 새로운 기록별 평균 계산
        if (this.totalRecordCount == 1) {
            // 첫 기록인 경우
            this.avgPositiveRatio = newPositiveRatio;
        } else {
            this.avgPositiveRatio = ((this.avgPositiveRatio * (this.totalRecordCount - 1)) + newPositiveRatio) / this.totalRecordCount;
        }
        // 추가된 기록이 오늘의 첫 기록일 시 totalRecordedDays와 lastRecordedDate 갱신
        if (lastRecordedDate == null || lastRecordedDate.isBefore(createdAt.toLocalDate())) {
            this.totalRecordedDays ++;
            this.lastRecordedDate = createdAt.toLocalDate();
        }
    }

    // 기록 삭제에 따른 UserAchievement 업데이트
    public void subtractRecord(float subtractedPositiveRatio, boolean isOnlyAnalysisOnSameDay) {

        // 전체 기록 수 감소
        this.totalRecordCount--;

        // 기록별 평균 계산
        if (this.totalRecordCount > 0) {
            this.avgPositiveRatio = ((this.avgPositiveRatio * (this.totalRecordCount + 1)) - subtractedPositiveRatio) / this.totalRecordCount;
        } else {
            this.avgPositiveRatio = 0;
        }

        // 삭제된 기록이 해당 날짜의 유일한 기록이라면 totalRecordedDays 감소
        if (isOnlyAnalysisOnSameDay) {
            this.totalRecordedDays--;
        }
    }

    // 기록 삭제로 인한 변경 발생 시, 삭제 이후 상황에 대해 lastRecordedDate 재계산하여 할당
    public void updateLastRecordedDate(LocalDate newLastRecordedDate) {
        this.lastRecordedDate = newLastRecordedDate;
    }

    public void increaseCurrentBadgeCount() {
        this.currentBadgeCount++;
    }
}
