package com.speako.external.nlp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class NlpAnalysisResponse {
    private final Long transcriptId;

    private final List<String> negativeSentences;
    private final List<String> negativeWords;

    private final double negativeRatio;
    private final double positiveRatio;
}
