package com.speako.event.nlp;

import com.speako.domain.analysis.entity.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.record.entity.Transcription;
import com.speako.domain.record.entity.enums.TranscriptionStatus;
import com.speako.domain.record.repository.TranscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class NlpCompletedEventHandler {

    public static String threadName;

    private final TranscriptionRepository transcriptionRepository;
    private final AnalysisRepository analysisRepository;

    @Async("nlpTaskExecutor")
    @EventListener
    public void handleNlpCompleted(NlpCompletedEvent event) {
        System.out.println("nlp handler thread: " + Thread.currentThread().getName());
        threadName = Thread.currentThread().getName();

        log.info("[NLP 분석 완료 이벤트 수신] transcriptionId={} / 욕설={}개",
                event.getTranscriptionId(),
                event.getNegativeWords().size()
        );

        // Transcription 조회
        Transcription transcription = transcriptionRepository.findById(event.getTranscriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transcriptionId"));

        // 분석 결과 저장
        // TODO: negativeSentences(new ArrayList<>()) 수정
        Analysis analysis = Analysis.builder()
                .transcription(transcription)
                .negativeSentences(new ArrayList<>())
                .negativeWords(event.getNegativeWords())
                .negativeRatio(event.getNegativeRatio())
                .positiveRatio(event.getPositiveRatio())
                .build();

        analysisRepository.save(analysis);

        // 상태 업데이트
        transcription.updateTranscriptionStatus(TranscriptionStatus.ANALYSIS_COMPLETED);
        transcriptionRepository.save(transcription);

        //TODO: FCM 푸시 전송
    }
}