package com.speako.domain.analysis.dto.reqDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NlpAnalysisResult(

        @JsonProperty("positive_ratio")
        Float positiveRatio,
        @JsonProperty("negative_ratio")
        Float negativeRatio,
        @JsonProperty("neutral_ratio")
        Float neutralRatio,
        @JsonProperty("negative_sentence")
        List<String> negativeSentences
) {
}
