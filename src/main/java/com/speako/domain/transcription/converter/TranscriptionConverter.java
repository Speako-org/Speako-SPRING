package com.speako.domain.transcription.converter;

import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.transcription.dto.resDTO.TranscriptionResDTO;

public class TranscriptionConverter {

    // Record -> RecordResDTO
    public static TranscriptionResDTO toTranscriptionResDTO(Transcription transcription, String s3PathUrl) {

        return new TranscriptionResDTO(
                transcription.getId(),
                transcription.getUser().getId(),
                transcription.getRecord().getId(),
                transcription.getTitle(),
                transcription.getStartTime(),
                transcription.getEndTime(),
                transcription.getTranscriptionStatus(),
                transcription.getCreatedAt(),
                s3PathUrl);
    }
}
