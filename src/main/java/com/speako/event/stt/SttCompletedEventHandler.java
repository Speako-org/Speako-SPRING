package com.speako.event.stt;

import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.external.nlp.NlpAnalysisClient;
import com.speako.external.nlp.NlpAnalysisResponse;
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

    private final NlpAnalysisClient nlpApiClient;
    private final ApplicationEventPublisher eventPublisher;
    private final TranscriptionRepository transcriptionRepository;

    @Async("sttTaskExecutor")
    @EventListener
    public void handleSttCompleted(SttCompletedEvent event) {
        System.out.println("stt handler thread: " + Thread.currentThread().getName());
        threadName = Thread.currentThread().getName();

        log.info(
                "[STT 이벤트 수신] transcriptionId={} / 텍스트 길이={} → NLP 서버 분석 요청",
                event.getTranscriptionId(), event.getText().length()
        );

        try {
            // NLP 서버 분석 요청
            NlpAnalysisResponse result = nlpApiClient.analyze(event.getText());

            // 분석 완료 이벤트 발행
            eventPublisher.publishEvent(new NlpCompletedEvent(
                    event.getTranscriptionId(),
                    result.getNegativeWords(),
                    result.getNegativeRatio(),
                    result.getPositiveRatio()
            ));
        } catch (Exception e) {
            log.error("[NLP 분석 실패] transcriptionId={} / 이유={}",
                    event.getTranscriptionId(),
                    e.getMessage(), e);

            transcriptionRepository.findById(event.getTranscriptionId())
                    .ifPresent(transcription -> {
                        transcription.updateTranscriptionStatus(ANALYSIS_FAIL);
                        transcriptionRepository.save(transcription);
                    });
        }
    }
}
