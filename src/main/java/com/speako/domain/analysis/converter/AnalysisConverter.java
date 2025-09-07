package com.speako.domain.analysis.converter;

import com.speako.domain.analysis.dto.resDTO.AnalysisResponseDTO;
import com.speako.domain.analysis.dto.resDTO.DailyRatioOfRecent7Days;
import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.transcription.domain.Transcription;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalysisConverter {

    public static AnalysisResponseDTO toAnalysisResponseDTO(
            Long userId,
            Transcription transcription,
            Analysis analysis,
            Float averageNegativeRatioOf7DaysAgo,
            Float averageNegativeRatioOfToday,
            List<DailyRatioOfRecent7Days> dailyRatioOfRecent7Days) {

        return AnalysisResponseDTO.builder()
                .userId(userId)
                .transcriptionId(transcription.getId())
                .analysisId(analysis.getId())
                .title(transcription.getTitle())
                .thumbnailText(transcription.getThumbnailText())
                .negativeRatio(analysis.getNegativeRatio())
                .positiveRatio(analysis.getPositiveRatio())
                .negativeSentencesTop3(analysis.getNegativeSentences())
                .feedbackSentences(analysis.getFeedbackSentences())
                .averageNegativeRatioOf7DaysAgo(averageNegativeRatioOf7DaysAgo)
                .averageNegativeRatioOfToday(averageNegativeRatioOfToday)
                .dailyRatioOfRecent7Days(dailyRatioOfRecent7Days)
                .build();
    }
}
