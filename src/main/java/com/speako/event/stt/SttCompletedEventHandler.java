package com.speako.event.stt;

import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.external.nlp.NlpAnalysisClient;
import com.speako.external.nlp.NlpAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SttCompletedEventHandler {

    private final NlpAnalysisClient nlpApiClient;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void handleSttCompleted(SttCompletedEvent event) {
        log.info(
                "[STT 이벤트 수신] transcriptId={} / 텍스트 길이={} → NLP 서버 분석 요청",
                event.getTranscriptId(), event.getText().length()
        );

        // NLP 서버 분석 요청
        NlpAnalysisResponse result = nlpApiClient.analyze(event.getText());

        // 분석 완료 이벤트 발행
        eventPublisher.publishEvent(new NlpCompletedEvent(
                event.getTranscriptId(),
                result.getNegativeSentences(),
                result.getNegativeWords(),
                result.getNegativeRatio(),
                result.getPositiveRatio()
        ));
    }
}
