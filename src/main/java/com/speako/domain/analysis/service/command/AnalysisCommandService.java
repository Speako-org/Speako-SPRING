package com.speako.domain.analysis.service.command;

import com.speako.domain.analysis.dto.reqDTO.NlpAnalysisResult;
import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.exception.AnalysisErrorCode;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.challenge.service.command.UserChallengeService;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.domain.userinfo.service.command.UserInfoCommandService;
import com.speako.domain.userinfo.service.command.monthlyStat.MonthlyStatCommandService;
import com.speako.domain.userinfo.service.command.userAchievement.UserAchievementCommandService;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.speako.domain.transcription.domain.enums.TranscriptionStatus.ANALYSIS_COMPLETED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisCommandService {
    private final AnalysisRepository analysisRepository;
    private final TranscriptionRepository transcriptionRepository;
    private final UserInfoCommandService userInfoCommandService;
    private final UserChallengeService userChallengeService;
    private final MonthlyStatCommandService monthlyStatCommandService;
    private final UserAchievementCommandService userAchievementCommandService;

    private Analysis saveAnalysis(Transcription transcription, NlpAnalysisResult result) {
        return analysisRepository.save(
                Analysis.builder()
                        .transcription(transcription)
                        .positiveRatio(result.positiveRatio())
                        .negativeRatio(result.negativeRatio())
                        .neutralRatio(result.neutralRatio())
                        .negativeSentences(result.negativeSentences())
                        .feedbackSentences(result.feedback())
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
      
        // updateUserInfo 호출 (내부에서 monthlyStat과 userAchievement 업데이트 수행)
        userInfoCommandService.updateUserInfo(analysis);

        userChallengeService.updateChallengeProgress(transcription.getRecord().getUser(), analysis);

        //TODO: FCM
    }

    // Analysis soft 삭제 처리 및 관련 통계치 rollback
    public void softDeleteAnalysis(Long userId, Long transcriptionId) {

        Analysis analysis = analysisRepository.findByTranscriptionId(transcriptionId)
                .orElseThrow(() -> new CustomException(AnalysisErrorCode.ANALYSIS_NOT_FOUND));
        analysis.updateDeletedAt(LocalDateTime.now());

        // 통계치(MonthlyStat, UserAchievement) rollback
        monthlyStatCommandService.rollbackMonthlyStat(userId, analysis);
        userAchievementCommandService.rollbackUserAchievement(analysis);
    }
}
