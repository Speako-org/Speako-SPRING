package com.speako.domain.analysis.dto.reqDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NlpCallbackRequestDTO(

        @JsonProperty("transcription_id")
        Long transcriptionId,
        NlpAnalysisResult data
) {
}
