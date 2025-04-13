package com.speako.domain.record.repository;

import com.speako.domain.record.entity.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
}
