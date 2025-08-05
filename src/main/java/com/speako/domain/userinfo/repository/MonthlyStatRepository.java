package com.speako.domain.userinfo.repository;

import com.speako.domain.userinfo.domain.MonthlyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MonthlyStatRepository extends JpaRepository<MonthlyStat, Integer> {

    // 최신 월 포함 6개월치 긍정&부정 비율 조회
    @Query("""
            SELECT m FROM MonthlyStat m WHERE m.user.id = :userId AND
            (m.year > :startYear OR (m.year = :startYear AND m.month >= :startMonth)) AND
            (m.year < :endYear OR (m.year = :endYear AND m.month <= :endMonth))
            ORDER BY m.year DESC, m.month DESC
            """)
    List<MonthlyStat> findByUserIdAndYearMonthBetween(
            @Param("userId") Long userId,
            @Param("startYear") int startYear,
            @Param("startMonth") int startMonth,
            @Param("endYear") int endYear,
            @Param("endMonth") int endMonth
    );

    @Query("SELECT m FROM MonthlyStat m WHERE m.user.id = :userId AND m.year = :year AND m.month = :month")
    Optional<MonthlyStat> findByUserIdAndYearAndMonth(@Param("userId") Long userId,
                                                      @Param("year") int year,
                                                      @Param("month") int month);
}
