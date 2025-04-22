package com.speako.domain.transcription.dto.reqDTO;

public record TranscribeReqDTO(

        Long transcriptionId,
        String recordS3Path
) {
}
