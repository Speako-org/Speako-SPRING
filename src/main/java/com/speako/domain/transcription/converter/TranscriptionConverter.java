package com.speako.domain.transcription.converter;

import com.speako.domain.transcription.dto.resDTO.TranscriptionResDTO;
import com.speako.domain.transcription.domain.Transcription;

import java.util.List;

public class TranscriptionConverter {

    // Record -> RecordResDTO
    public static TranscriptionResDTO toTranscriptionResDTO(Transcription transcription) {

        return new TranscriptionResDTO(
                transcription.getId(),
                transcription.getUser().getId(),
                transcription.getRecord().getId(),
                transcription.getTitle(),
                transcription.getStartTime(),
                transcription.getEndTime(),
                transcription.getTranscriptionStatus(),
                transcription.getCreatedAt());
    }

    // List<Record> -> List<RecordResDTO>
    public static List<TranscriptionResDTO> toTranscriptionResDTOList(List<Transcription> transcriptions) {

        return transcriptions.stream()
                .map(TranscriptionConverter::toTranscriptionResDTO)
                .toList();
    }
}
