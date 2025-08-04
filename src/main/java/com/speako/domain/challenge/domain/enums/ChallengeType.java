package com.speako.domain.challenge.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ChallengeType {
    CONTINUOUS_RECORD("연속 기록", 0),
    POSITIVE_EXPRESSION("긍정 표현 달성", 5),
    NEGATIVE_REDUCTION("부정 감소", 10),
    RECORD_COUNT("기록 수 달성", 15);

    private final String displayName;
    private final int badgeStartIndex;


    public static ChallengeType ofDisplayName(String displayName) {
        return Arrays.stream(ChallengeType.values())
                .filter(c -> c.getDisplayName().equals(displayName))
                .findAny()
                .orElse(null);
    }

    public int getBadgeIndex(int level) {
        return badgeStartIndex + (level - 1);
    }
}
