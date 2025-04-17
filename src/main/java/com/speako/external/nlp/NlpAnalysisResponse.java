package com.speako.external.nlp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class NlpAnalysisResponse {
    private final Long transcriptionId;

    private final List<String> negativeWords;

    private final float negativeRatio;
    private final float positiveRatio;
}
