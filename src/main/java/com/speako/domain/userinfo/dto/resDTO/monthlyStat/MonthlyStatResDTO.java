package com.speako.domain.userinfo.dto.resDTO.monthlyStat;

// 마이페이지 속 유저의 현재~과거 월 통계 DTO
public record MonthlyStatResDTO(

        int year,
        int month,
        float avgPositiveRatio,
        float avgNegativeRatio,
        int maxStreak
) {}