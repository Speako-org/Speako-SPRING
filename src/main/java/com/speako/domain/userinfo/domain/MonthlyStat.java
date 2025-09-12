package com.speako.domain.userinfo.domain;

import com.speako.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "total_record_count", nullable = false)
    private int totalRecordCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 긍정/부정 비율 가산하여 평균 계산
    public void addRatios(float newPositiveRatio, float newNegativeRatio) {

        if (this.totalRecordCount == 0) {
            // 첫 기록인 경우 그대로 할당
            this.avgPositiveRatio = newPositiveRatio;
            this.avgNegativeRatio = newNegativeRatio;
        } else {
            // 누적 평균 계산
            this.avgPositiveRatio = ((this.avgPositiveRatio * this.totalRecordCount) + newPositiveRatio) / (this.totalRecordCount + 1);
            this.avgNegativeRatio = ((this.avgNegativeRatio * this.totalRecordCount) + newNegativeRatio) / (this.totalRecordCount + 1);
        }
        // 총 녹음기록 수 증가
        this.totalRecordCount++;
    }

    // 긍정/부정 비율 감산하여 평균 계산
    public void subtractRatios(float subtractedPositiveRatio, float subtractedNegativeRatio) {

        if (this.totalRecordCount > 1) {
            this.avgPositiveRatio = ((this.avgPositiveRatio * this.totalRecordCount) - subtractedPositiveRatio) / (this.totalRecordCount - 1);
            this.avgNegativeRatio = ((this.avgNegativeRatio * this.totalRecordCount) - subtractedNegativeRatio) / (this.totalRecordCount - 1);
            this.totalRecordCount --;
        } else {
            // 이번 달 기록 수가 하나이거나(감산 후 0개가 될 예정), 0개이거나(불일치 상황), 음수인 경우(불일치 상황) -> 모든 통계 값은 0이어야 함
            this.totalRecordCount = 0;
            this.avgPositiveRatio = 0;
            this.avgNegativeRatio = 0;
        }
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

    // 기록 삭제에 따른 currentStreak 재계산
    public void calculateCurrentStreak(LocalDate analysisCreatedAt, boolean isOnlyAnalysisOnSameDay, List<LocalDateTime> recordedDatesInMonth) {

        // 가장 최근 기록일
        LocalDate latestRecordedDate = recordedDatesInMonth.stream()
                .map(LocalDateTime::toLocalDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        // 이번 달의 기록이 없을 시 streak 값 초기화
        if (latestRecordedDate == null) {
            this.currentStreak = 0;
            this.maxStreak = 0;

        // currentStreak, maxStreak 갱신
        } else {
            /*
                currentStreak 범위 날짜 리스트 생성
                ex) currentStreak=3, latest=9/12 -> [9/10, 9/11, 9/12]
                ex) currentStreak=3, latest=9/11 -> [9/9, 9/10, 9/11]
             */
            List<LocalDate> streakDates = new ArrayList<>();
            for (int i = 0; i < this.currentStreak; i++) {
                streakDates.add(latestRecordedDate.minusDays(this.currentStreak - 1 - i));
            }
            // 삭제 날짜가 streak 범위에 포함되는지 확인
            int index = streakDates.indexOf(analysisCreatedAt);
            // 삭제된 날짜 포함 && 삭제하려는 기록이 해당 날짜의 유일한 기록 → streak 변경 발생
            if (index != -1 && isOnlyAnalysisOnSameDay) {
                this.currentStreak = streakDates.size() - (index + 1);
            }
        }
    }

    // 기록 삭제에 따른 maxStreak 재계산
    public void calculateMaxStreak(List<LocalDateTime> recordedDates) {

        int newMaxStreak = 1;
        int current = 1;
        List<LocalDate> sorted = recordedDates.stream()
                .map(LocalDateTime::toLocalDate)
                .sorted()
                .toList();

        // 이번 달의 기록된 날짜 리스트를 사용하여, 가장 길게 연속기록된 날 수 계산
        for (int i = 1; i < sorted.size(); i++) {

            LocalDate prev = sorted.get(i - 1);
            LocalDate curr = sorted.get(i);

            if (curr.equals(prev.plusDays(1))) {
                current++;
                newMaxStreak = Math.max(newMaxStreak, current);
            } else {
                current = 1;
            }
        }
        this.maxStreak = newMaxStreak;
    }
}
