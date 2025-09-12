package com.speako.domain.userinfo.service.command.monthlyStat;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.service.query.AnalysisQueryService;
import com.speako.domain.user.domain.User;
import com.speako.domain.userinfo.domain.MonthlyStat;
import com.speako.domain.userinfo.exception.MonthlyStatErrorCode;
import com.speako.domain.userinfo.repository.MonthlyStatRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyStatCommandService {

    private final MonthlyStatRepository monthlyStatRepository;
    private final AnalysisQueryService analysisQueryService;

    // MonthlyStat update
    public void updateMonthlyStat(Analysis analysis) {

        User user = analysis.getTranscription().getUser();
        LocalDateTime createdAt = analysis.getCreatedAt();
        int year = createdAt.getYear();
        int month = createdAt.getMonthValue();
        LocalDate today = createdAt.toLocalDate();

        // 현재 달에 해당하는 MonthlyStat 조회 (없으면 최초 생성)
        MonthlyStat stat = monthlyStatRepository.findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseGet(() -> MonthlyStat.builder()
                        .user(user)
                        .year(year)
                        .month(month)
                        .build());
        // 긍정/부정 비율 가산
        stat.addRatios(analysis.getPositiveRatio(), analysis.getNegativeRatio());
        // 현재 달의 연속 기록일 수(현재&최장) update
        updateStreak(stat, user, today);

        monthlyStatRepository.save(stat);
    }

    // MonthlyStat rollback
    public void rollbackMonthlyStat(Long userId, Analysis analysis) {

        // Record 기준 month/year
        LocalDateTime createdAt = analysis.getCreatedAt();
        int year = createdAt.getYear();
        int month = createdAt.getMonthValue();

        MonthlyStat monthlyStat = monthlyStatRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .orElseThrow(() -> new CustomException(MonthlyStatErrorCode.MONTHLY_STAT__NOT_FOUND));

        // 긍정/부정 비율 및 totalRecordCount 감산
        monthlyStat.subtractRatios(analysis.getPositiveRatio(), analysis.getNegativeRatio());

        // 해당 analysis가 자신이 속하는 날짜의 유일한 analysis인가?
        boolean isOnlyAnalysisOnSameDay = analysisQueryService.isOnlyAnalysisOnSameDay(userId, analysis.getCreatedAt());
        // 이번 달 기록이 존재하는 날짜(LocalDate) 리스트
        List<LocalDateTime> recordedDatesInMonth = analysisQueryService.recordedDatesInMonth(userId);

        // currentStreak, maxStreak 갱신
        monthlyStat.calculateCurrentStreak(analysis.getCreatedAt().toLocalDate(), isOnlyAnalysisOnSameDay, recordedDatesInMonth);
        monthlyStat.calculateMaxStreak(recordedDatesInMonth);
    }

    // 현재 달의 연속 기록일 수(현재&최장) update
    private void updateStreak(MonthlyStat stat, User user, LocalDate today) {

        LocalDate yesterday = today.minusDays(1);
        LocalDate lastStreakUpdateDate = stat.getLastStreakUpdateDate();

        // 오늘 갱신된 경우 재갱신 방지
        if (lastStreakUpdateDate != null && lastStreakUpdateDate.equals(today)) {
            return;
        }

        // 연속 기록일 갱신 (이미 오늘 갱신된 경우 조건 포함 X)
        if (lastStreakUpdateDate == null || lastStreakUpdateDate.isBefore(yesterday)) {
            stat.initCurrentStreak();
        } else if (lastStreakUpdateDate.equals(yesterday)) {
            stat.addCurrentStreak();
        }

        stat.updateLastStreakUpdateDate(today);
    }
}
