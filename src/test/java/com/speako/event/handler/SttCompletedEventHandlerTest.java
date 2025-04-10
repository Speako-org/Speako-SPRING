package com.speako.event.handler;

import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.event.stt.SttCompletedEvent;
import com.speako.event.stt.SttCompletedEventHandler;
import com.speako.external.nlp.NlpApiClient;
import com.speako.external.nlp.NlpAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class SttCompletedEventHandlerTest {

    @Mock
    private NlpApiClient nlpApiClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SttCompletedEventHandler handler;

    private SttCompletedEvent sttCompletedEvent;

    @BeforeEach
    void setUp() {
        sttCompletedEvent = new SttCompletedEvent(
                1L,
                "넌 진짜 못됐어"
        );
    }

    @Test
    void handleSttCompleted_테스트() {
        // given
        NlpAnalysisResponse fakeResponse = new NlpAnalysisResponse(
                1L,
                List.of("넌 진짜 못됐어"),
                List.of("못됐어"),
                0.8,
                0.1
        );
        when(nlpApiClient.analyze(sttCompletedEvent.getText())).thenReturn(fakeResponse);

        // when
        handler.handleSttCompleted(sttCompletedEvent);

        // then
        // NLP API 호출 검증
        verify(nlpApiClient, times(1)).analyze(sttCompletedEvent.getText());

        // 이벤트 발행 검증
        ArgumentCaptor<NlpCompletedEvent> captor = ArgumentCaptor.forClass(NlpCompletedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        NlpCompletedEvent publishedEvent = captor.getValue();
        assertEquals(sttCompletedEvent.getTranscriptId(), publishedEvent.getTranscriptId());
        assertEquals(fakeResponse.getNegativeSentences(), publishedEvent.getNegativeSentences());
        assertEquals(fakeResponse.getNegativeWords(), publishedEvent.getNegativeWords());
        assertEquals(fakeResponse.getNegativeRatio(), publishedEvent.getNegativeRatio());
        assertEquals(fakeResponse.getPositiveRatio(), publishedEvent.getPositiveRatio());
    }
}