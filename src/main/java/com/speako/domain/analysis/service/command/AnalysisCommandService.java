package com.speako.domain.analysis.service.command;

import com.speako.domain.analysis.dto.reqDTO.NlpAnalysisResult;
import com.speako.domain.analysis.entity.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.transcription.entity.Transcription;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.speako.domain.transcription.entity.enums.TranscriptionStatus.ANALYSIS_COMPLETED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisCommandService {
    private final AnalysisRepository analysisRepository;
    private final TranscriptionRepository transcriptionRepository;

    private Analysis saveAnalysis(Transcription transcription, NlpAnalysisResult result) {
        return analysisRepository.save(
                Analysis.builder()
                        .transcription(transcription)
                        .positiveRatio(result.positiveRatio())
                        .negativeRatio(result.negativeRatio())
                        .neutralRatio(result.neutralRatio())
                        .negativeSentences(result.negativeSentences())
                        .build()
        );
    }

    public void handleAnalysisCallback(Long transcriptionId, NlpAnalysisResult result) {
        Transcription transcription = transcriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transcriptionId"));

        // 1. Transcription 상태 변경
        transcription.updateTranscriptionStatus(ANALYSIS_COMPLETED);

        // 2. 분석 결과 저장
        Analysis analysis = saveAnalysis(transcription, result);

        log.info("[NLP 분석 완료] analysisId={} / transcriptionId={}", analysis.getId(), transcription.getId());

        //TODO: FCM
    }

}
