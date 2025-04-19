package com.speako.domain.record.entity;

import com.speako.domain.record.entity.enums.RecordStatus;
import com.speako.domain.user.entity.User;
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
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "s3_path", columnDefinition = "TEXT")
    private String s3Path;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecordStatus recordStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // recordStatus 업데이트 (상태 변경)
    public void updateRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    // s3Path 업데이트
    public void updateRecordS3Path(String s3Path) {
        this.s3Path = s3Path;
    }
}
