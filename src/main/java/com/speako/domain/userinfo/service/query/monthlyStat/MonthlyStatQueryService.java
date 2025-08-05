package com.speako.domain.userinfo.service.query.monthlyStat;

import com.speako.domain.userinfo.domain.MonthlyStat;
import com.speako.domain.userinfo.dto.resDTO.monthlyStat.MonthlyStatResDTO;
import com.speako.domain.userinfo.repository.MonthlyStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MonthlyStatQueryService {

    private final MonthlyStatRepository monthlyStatRepository;

    // 현재 달 포함하여 근 6개월 MonthlyStats를 조회
    public List<MonthlyStatResDTO> get6RecentMonthlyStats(Long userId) {

        YearMonth current = YearMonth.now();
        YearMonth minDate = current.minusMonths(5);

        // 최신 월 포함 6개월치 긍정&부정 비율 조회
        List<MonthlyStat> monthlyStats = monthlyStatRepository.findByUserIdAndYearMonthBetween(
                userId,
                minDate.getYear(), minDate.getMonthValue(),
                current.getYear(), current.getMonthValue()
        );

        return monthlyStats.stream()
                .map(monthlyStat -> new MonthlyStatResDTO(
                        monthlyStat.getYear(),
                        monthlyStat.getMonth(),
                        monthlyStat.getAvgPositiveRatio(),
                        monthlyStat.getAvgNegativeRatio(),
                        monthlyStat.getMaxStreak()
                )).toList();
    }
}
