package com.speako.event.stt;

import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.external.nlp.NlpAnalyzeClient;
import com.speako.external.nlp.NlpAnalyzeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.speako.domain.transcription.entity.enums.TranscriptionStatus.ANALYSIS_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class SttCompletedEventHandler {

    public static String threadName;

    private final NlpAnalyzeClient nlpAnalyzeClient;
    private final ApplicationEventPublisher eventPublisher;
    private final TranscriptionRepository transcriptionRepository;

    @Async("sttTaskExecutor")
    @EventListener
    public void handleSttCompleted(SttCompletedEvent event) {
        //테스트용
        threadName = Thread.currentThread().getName();

        log.info(
                "[STT 완료 이벤트 수신] transcriptionId={} → NLP 서버 분석 요청",
                event.getTranscriptionId()
        );

        //TODO: transcription 상태 변경 코드 추가
        //      transcriptionService.updateStatus(event.getTranscriptionId(), NLP_IN_PROGRESS);

        nlpAnalyzeClient.analyze(event.getTranscriptionId() , event.getTranscriptionS3Path());

        log.info("[NLP 분석 요청] transcriptionId={}", event.getTranscriptionId());
    }
}
