package com.speako.domain.analysis.repository;

import com.speako.domain.analysis.domain.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    @Query("""
        SELECT a FROM Analysis a
        WHERE a.transcription.startTime >= :startDateTime
            AND a.transcription.endTime < :endDateTime
            AND a.deletedAt IS NULL
    """)
    List<Analysis> findAllByTranscriptionCreatedAtBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT a FROM Analysis a WHERE a.transcription.id = :transcriptionId AND a.deletedAt IS NULL")
    Optional<Analysis> findByTranscriptionId(@Param("transcriptionId") Long transcriptionId);

    @Query("""
        SELECT COUNT(a) FROM Analysis a
        WHERE a.transcription.user.id = :userId
          AND DATE(a.createdAt) = DATE(:createdAt)
          AND a.deletedAt IS NULL
    """)
    //
    int countAnalysesOnSameDay(
            @Param("userId") Long userId,
            @Param("createdAt") LocalDateTime createdAt
    );

    @Query(value = """
        SELECT a.created_at FROM analysis a
        JOIN transcription t ON a.transcription_id = t.id
        WHERE t.user_id = :userId
          AND DATE(a.created_at) = (
              SELECT MAX(DATE(a2.created_at)) FROM analysis a2
              JOIN transcription t2 ON a2.transcription_id = t2.id
              WHERE t2.user_id = :userId 
                AND a2.deleted_at IS NULL)
          AND a.deleted_at IS NULL
        ORDER BY a.created_at ASC
        LIMIT 1
    """, nativeQuery = true)
    // TODO analysis에 user fk로 가지도록 수정하고 싶다... 비효율적임
    // 특정 유저의 Analysis 중에서, 가장 최근 기록일(lastRecordedDate)의 첫 번째 기록의 createdAt
    Optional<LocalDateTime> findFirstCreatedAtOfLastRecordedDate(@Param("userId") Long userId);

    @Query(value = """
        SELECT DISTINCT a.createdAt FROM Analysis a
        JOIN a.transcription t
        WHERE t.user.id = :userId
          AND a.deletedAt IS NULL
          AND a.createdAt BETWEEN :startOfMonth AND :endOfMonth
    """)
    List<LocalDateTime> findRecordedDatesInMonth(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth
    );
}

