package com.speako.event.handler;

import com.speako.domain.analysis.entity.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.record.entity.Transcription;
import com.speako.domain.record.entity.enums.TranscriptionStatus;
import com.speako.domain.record.repository.TranscriptionRepository;
import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.event.nlp.NlpCompletedEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NlpCompletedEventHandlerTest {

    @Mock
    private TranscriptionRepository transcriptionRepository;

    @Mock
    private AnalysisRepository analysisRepository;

    @InjectMocks
    private NlpCompletedEventHandler handler;

    private NlpCompletedEvent nlpCompletedEvent;

    @BeforeEach
    void setup() {
        nlpCompletedEvent = new NlpCompletedEvent(
                1L,
                List.of("테스트"),
                0.1f,
                0.1f
        );
    }

    @Test
    public void transcription_조회() {
        //given
        Transcription transcription = Transcription.builder()
                .transcriptionStatus(TranscriptionStatus.ANALYSIS_IN_PROGRESS)
                .build();

        when(transcriptionRepository.findById(any())).thenReturn(Optional.of(transcription));

        //when
        handler.handleNlpCompleted(nlpCompletedEvent);

        //then
        verify(analysisRepository, times(1))
                .save(any(Analysis.class));

        assertEquals(TranscriptionStatus.ANALYSIS_COMPLETED,
                transcription.getTranscriptionStatus());

        verify(transcriptionRepository, times(1))
                .save(transcription);
    }


    @Test
    public void transcription_조회X() {
        //given
        when(transcriptionRepository.findById(any()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> handler.handleNlpCompleted(nlpCompletedEvent));

        verify(analysisRepository, never())
                .save(any());

        verify(transcriptionRepository, never())
                .save(any());
    }
}