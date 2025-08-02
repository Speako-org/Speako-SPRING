package com.speako.domain.transcription.domain;

import com.speako.domain.record.domain.Record;
import com.speako.domain.transcription.domain.enums.TranscriptionStatus;
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
public class Transcription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    @Column(name = "s3_path", columnDefinition = "TEXT")
    private String s3Path;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "thumbnail_text")
    private String thumbnailText;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TranscriptionStatus transcriptionStatus;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // TranscriptionStatus 업데이트 (상태 변경)
    public void updateTranscriptionStatus(TranscriptionStatus transcriptionStatus) {
        this.transcriptionStatus = transcriptionStatus;
    }

    // s3Path 업데이트
    public void updateTranscriptionS3Path(String s3Path) {
        this.s3Path = s3Path;
    }

    // thumbnailText 업데이트
    public void updateThumbnailText(String thumbnailText) {
        this.thumbnailText = thumbnailText;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
