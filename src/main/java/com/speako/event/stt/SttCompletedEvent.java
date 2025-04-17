package com.speako.event.stt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SttCompletedEvent {

    private final Long transcriptionId;
    private final String text;
}
