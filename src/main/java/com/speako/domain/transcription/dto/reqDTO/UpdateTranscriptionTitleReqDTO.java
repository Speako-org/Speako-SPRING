package com.speako.domain.transcription.dto.reqDTO;

import jakarta.validation.constraints.NotBlank;

public record UpdateTranscriptionTitleReqDTO(

        @NotBlank(message = "변경될 녹음기록의 title은 공백일 수 없습니다.")
        String newTranscriptionTitle
) {
}
