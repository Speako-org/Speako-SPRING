package com.speako.domain.analysis.dto.reqDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NlpAnalysisResult(

        @JsonProperty("negative_words")
        List<String> negativeWords,
        @JsonProperty("negative_ratio")
        Float negativeRatio,
        @JsonProperty("positive_ratio")
        Float positiveRatio
) {
}
