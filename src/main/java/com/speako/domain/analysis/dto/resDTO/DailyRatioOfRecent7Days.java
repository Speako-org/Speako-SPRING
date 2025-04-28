package com.speako.domain.analysis.dto.resDTO;

import java.time.LocalDate;

public record DailyRatioOfRecent7Days(

        LocalDate date,
        Float avgNegativeRatio,
        Float avgPositiveRatio
) {
}
