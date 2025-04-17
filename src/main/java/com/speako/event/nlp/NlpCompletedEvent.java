package com.speako.event.nlp;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class NlpCompletedEvent {

    private final Long transcriptionId;

    private final List<String> negativeWords;

    private final float negativeRatio;
    private final float positiveRatio;

}
