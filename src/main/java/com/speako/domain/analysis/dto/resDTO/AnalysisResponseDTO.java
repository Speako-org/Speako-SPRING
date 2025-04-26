package com.speako.domain.analysis.dto.resDTO;

import lombok.Builder;

import java.util.List;

@Builder
// 음성 기록 분석 결과 조회 시에 사용
public record AnalysisResponseDTO(

//        Long userId,
        Long transcriptionId,
        Long analysisId,
        String title,
        String thumbnailText, // 전체 텍스트의 앞부분
        Float negativeRatio, // 전체 중 부정적 표현 사용 비율
        Float positiveRatio, // 전체 중 긍정적 표현 사용 비율
        List<String> negativeWordsTop3, // 전체 부정적 표현 중 제일 많이 사용된 표현 Top3
        Float averageNegativeRatioOf7DaysAgo, // 조회일 기준 7일 전 기록의 평균 부정적 표현 사용 비율 (null일 경우 기록이 존재하지 않는다고 가정)
        Float averageNegativeRatioOfToday, // 조회 당일 기록의 평균 부정적 표현 사용 비율 (null일 수 없음)
        List<DailyRatioOfRecent7Days> dailyRatioOfRecent7Days // 최근 7일간의 부정/긍정 표현 비율
) {
}
