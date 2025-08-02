package com.speako.domain.analysis.service.query;

import com.speako.domain.analysis.converter.AnalysisConverter;
import com.speako.domain.analysis.dto.resDTO.AnalysisResponseDTO;
import com.speako.domain.analysis.dto.resDTO.DailyRatioOfRecent7Days;
import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.exception.AnalysisErrorCode;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.transcription.exception.TranscriptionErrorCode;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisQueryService {

    private final TranscriptionRepository transcriptionRepository;
    private final AnalysisRepository analysisRepository;

    public AnalysisResponseDTO getAnalysis(Long transcriptionId) {

        Transcription transcription = transcriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new CustomException(TranscriptionErrorCode.TRANSCRIPTION_NOT_FOUND));
        Analysis analysis = analysisRepository.findByTranscriptionId(transcriptionId)
                .orElseThrow(() -> new CustomException(AnalysisErrorCode.ANALYSIS_NOT_FOUND));

        /*
            아래 세가지 값들 중 자주 업데이트되지 않는 값은 DB에 저장될 수 있도록(쿼리 감소 목적) 구현하여 성능 개선하는 쪽도 생각해볼 것
            (일단 Analysis 테이블 수정 후에!)

            1. averageNegativeRatioOf7DaysAgo: 조회일 기준 7일 전 기록의 평균 부정적 표현 사용 비율
            2. averageNegativeRatioOfToday: 조회 당일 기록의 평균 부정적 표현 사용 비율
            3. dailyRatioOfRecent7Days: 조회일 기준 최근 7일간의 부정/긍정 표현 비율
         */
        Float averageNegativeRatioOf7DaysAgo = calculateAverageNegativeRatioOf7DaysAgo(transcription.getStartTime());
        Float averageNegativeRatioOfToday = calculateAverageNegativeRatioOfToday(transcription.getStartTime());
        List<DailyRatioOfRecent7Days> dailyRatioOfRecent7Days = toDailyRatioOfRecent7DaysList(transcription.getStartTime());

        return AnalysisConverter.toAnalysisResponseDTO(transcription, analysis, averageNegativeRatioOf7DaysAgo, averageNegativeRatioOfToday, dailyRatioOfRecent7Days);
    }

    // 조회일 기준 7일 전 기록의 평균 부정적 표현 사용 비율 구하기
    private Float calculateAverageNegativeRatioOf7DaysAgo(LocalDateTime startTime) {

        // 조회일 기준 일주일 전 날짜 구하기
        LocalDate sevenDaysAgo = startTime.toLocalDate().minusDays(7);

        // 해당 날짜에 생성된(transcription의 startTime이 그 사이에 포함되는) Transcription의 analysis들 조회
        List<Analysis> analyses = analysisRepository.findAllByTranscriptionCreatedAtBetween(
                sevenDaysAgo.atStartOfDay(),
                sevenDaysAgo.plusDays(1).atStartOfDay()
        );

        // 해당 날짜에 생성된 Analysis가 없으면 0.0f 반환, 있으면 NegativeRatio로 평균내기
        float sum = 0.0f;
        if (analyses.isEmpty()) {
            return sum;
        } else {
            for (Analysis a : analyses) {
                sum += a.getNegativeRatio();
            }
        }
        return sum / analyses.size();
    }

    // 조회 당일 기록의 평균 부정적 표현 사용 비율 구하기
    private Float calculateAverageNegativeRatioOfToday(LocalDateTime startTime) {

        // 조회 당일 날짜 구하기
        LocalDate todayDate = startTime.toLocalDate();

        // 해당 날짜에 생성된(transcription의 startTime이 그 사이에 포함되는) Transcription의 analysis들 조회
        List<Analysis> analyses = analysisRepository.findAllByTranscriptionCreatedAtBetween(
                todayDate.atStartOfDay(),
                todayDate.plusDays(1).atStartOfDay()
        );

        // 해당 날짜에 생성된 Analysis가 없으면 0.0f 반환, 있으면 NegativeRatio로 평균내기
        float sum = 0.0f;
        if (analyses.isEmpty()) {
            return sum;
        } else {
            for (Analysis a : analyses) {
                sum += a.getNegativeRatio();
            }
        }
        return sum / analyses.size();
    }

    // 조회일 기준 최근 7일간의 부정/긍정 표현 비율 리스트 구하기
    private List<DailyRatioOfRecent7Days> toDailyRatioOfRecent7DaysList(LocalDateTime startTime) {

        // 조회 당일 날짜 구하기
        LocalDate todayDate = startTime.toLocalDate();
        // 조회일 기준 6일 전을 시작 기준으로 함
        LocalDate sevenDaysAgoDate = todayDate.minusDays(6);

        // 해당 날짜에 생성된(transcription의 startTime이 그 사이에 포함되는) Transcription의 analysis들 조회
        List<Analysis> analyses = analysisRepository.findAllByTranscriptionCreatedAtBetween(
                sevenDaysAgoDate.atStartOfDay(),
                todayDate.plusDays(1).atStartOfDay()
        );

        // 각 날짜별로 Map 생성
        Map<LocalDate, List<Analysis>> groupedByDate = analyses.stream()
                .collect(Collectors.groupingBy(a -> a.getTranscription().getStartTime().toLocalDate()));

        List<DailyRatioOfRecent7Days> dailyRatioOfRecent7DaysList = new ArrayList<>();

        // 각 날짜별로 평균 부정/긍정 비율 계산 후 리스트에 add
        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgoDate.plusDays(i);
            List<Analysis> analysesByDate = groupedByDate.getOrDefault(date, Collections.emptyList());

            log.info("{}번째 날짜의 Analyses: {}", i, analysesByDate.toString());

            float avgNegativeRatio = 0.0f;
            float avgPositiveRatio = 0.0f;

            if (!analysesByDate.isEmpty()) {
                for (Analysis a : analysesByDate) {
                    avgNegativeRatio += a.getNegativeRatio();
                    avgPositiveRatio += a.getPositiveRatio();
                }
                avgNegativeRatio /= analysesByDate.size();
                avgPositiveRatio /= analysesByDate.size();
            }

            dailyRatioOfRecent7DaysList.add(new DailyRatioOfRecent7Days(date, avgNegativeRatio, avgPositiveRatio));
        }
        return dailyRatioOfRecent7DaysList;
    }
}
