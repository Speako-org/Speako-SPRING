package com.speako.domain.analysis.domain;

import com.speako.domain.transcription.domain.Transcription;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcription_id")
    private Transcription transcription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "negative_sentences", columnDefinition = "jsonb", updatable = false)
    private List<String> negativeSentences;

    @Column(name = "positive_ratio", nullable = false)
    private Float positiveRatio;

    @Column(name = "negative_ratio", nullable = false)
    private Float negativeRatio;

    @Column(name = "neutral_ratio", nullable = false)
    private Float neutralRatio;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
