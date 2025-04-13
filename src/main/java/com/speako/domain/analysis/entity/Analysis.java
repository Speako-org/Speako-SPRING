package com.speako.domain.analysis.entity;

import com.speako.domain.record.entity.Transcription;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "negative_words", columnDefinition = "jsonb", updatable = false)
    private List<String> negativeWords;

    @Column(name = "negative_ratio", nullable = false)
    private Float negativeRatio;

    @Column(name = "positive_ratio", nullable = false)
    private Float positiveRatio;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
