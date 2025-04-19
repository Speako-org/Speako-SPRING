package com.speako.event.handler;

import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.event.stt.SttCompletedEvent;
import com.speako.event.stt.SttCompletedEventHandler;
import com.speako.external.nlp.NlpApiClient;
import com.speako.external.nlp.NlpAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class SttCompletedEventHandlerTest {

    @Mock
    private NlpApiClient nlpApiClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TranscriptionRepository transcriptionRepository;

    @InjectMocks
    private SttCompletedEventHandler handler;

    private SttCompletedEvent sttCompletedEvent;

    @BeforeEach
    void setUp() {
        sttCompletedEvent = new SttCompletedEvent(
                1L,
                "테스트"
        );
    }

    @Test
    void 분석_성공시_이벤트_발행() {
        // given
        NlpAnalysisResponse mockResult = new NlpAnalysisResponse(
                1L,
                List.of("테스트"),
                0.1f,
                0.1f
        );
        when(nlpApiClient.analyze(sttCompletedEvent.getText())).thenReturn(mockResult);

        // when
        handler.handleSttCompleted(sttCompletedEvent);

        // then
        // nlp api 호출 검증
        verify(nlpApiClient, times(1))
                .analyze(sttCompletedEvent.getText());
        // 이벤트 발행 검증
        verify(eventPublisher, times(1))
                .publishEvent(any(NlpCompletedEvent.class));
    }

    @Test
    public void 분석_실패시_이벤트_발행X() {
        //given
        when(nlpApiClient.analyze(any()))
                .thenThrow(new RuntimeException("nlp 분석 실패"));
        when(transcriptionRepository.findById(any()))
                .thenReturn(Optional.of(mock()));
        //when

        //then
        assertDoesNotThrow(() -> handler.handleSttCompleted(sttCompletedEvent));
        verify(eventPublisher, never()).publishEvent(any());

        verify(transcriptionRepository, times(1)).findById(any());
        verify(transcriptionRepository, times(1)).save(any());
    }

}