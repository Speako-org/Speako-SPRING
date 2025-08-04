package com.speako.domain.challenge.domain;

import com.speako.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_challenges")
public class UserChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(nullable = false)
    private Integer amount = 0;

    @Column(name = "last_record_date")
    private LocalDate lastRecordDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateProgress(Integer incrementAmount) {
        this.amount += incrementAmount;
        this.lastRecordDate = LocalDate.now();
    }

    public void updateProgress(Integer incrementAmount, LocalDate today) {
        this.amount += incrementAmount;
        this.lastRecordDate = today;
    }

    public void complete() {
        this.isActive = false;
    }

    public boolean isAchieved() {
        return amount >= challenge.getRequiredAmount();
    }

    public boolean isConsecutiveChallenge() {
        return challenge.getName().equals("연속 기록");
    }

    public boolean isTodayRecorded(LocalDate today) {
        return lastRecordDate != null && lastRecordDate.equals(today);
    }

    public boolean isYesterdayRecorded(LocalDate today) {
        return lastRecordDate != null && lastRecordDate.equals(today.minusDays(1));
    }
}
