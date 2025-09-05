package com.speako.domain.transcription.repository;

import com.speako.domain.transcription.domain.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {

    // 특정 일에 생성된 녹음 기록 조회 (isDeleted 조건은 반영안됨)
    List<Transcription> findAllByUserIdAndCreatedAtBetweenOrderByCreatedAtAsc(Long userId, LocalDateTime start, LocalDateTime end);
}
