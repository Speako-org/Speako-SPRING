package com.speako.domain.analysis.repository;

import com.speako.domain.analysis.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    @Query("""
        SELECT a FROM Analysis a
        WHERE a.transcription.startTime >= :startDateTime
            AND a.transcription.endTime < :endDateTime
    """)
    List<Analysis> findAllByTranscriptionCreatedAtBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}

