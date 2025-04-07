package com.speako.event.stt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SttCompletedEvent {

    private final Long transcriptId;
    private final String text;
}
