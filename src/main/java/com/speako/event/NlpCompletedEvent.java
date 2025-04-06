package com.speako.event;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class NlpCompletedEvent {

    private final Long transcriptId;

    private final List<String> negativeSentences;
    private final List<String> negativeWords;

    private final double negativeRatio;
    private final double positiveRatio;

}
