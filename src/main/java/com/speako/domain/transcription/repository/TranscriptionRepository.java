package com.speako.domain.transcription.repository;

import com.speako.domain.transcription.entity.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
}
