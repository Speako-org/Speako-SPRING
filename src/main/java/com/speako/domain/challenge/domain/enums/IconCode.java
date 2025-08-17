package com.speako.domain.challenge.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IconCode {

    // 연속 기록 뱃지
    FIRST_STEP("👣"), // 첫걸음 뱃지
    CONSISTENCY("📅"), // 꾸준함 뱃지
    HABIT_POWER("💪"), // 습관의 힘 뱃지
    IRONMAN("🛡️"), // 철인 뱃지
    LEGEND("🏔️"), // 전설 뱃지

    // 긍정 표현 달성 뱃지
    POSITIVE_START("🌱"), // 긍정 시작 뱃지
    POSITIVE_UPGRADE("✨"), // 긍정 향상 뱃지
    POSITIVE_MASTER("🎓"), // 긍정 마스터 뱃지
    POSITIVE_EXPERT("🏆"), // 긍정 전문가 뱃지
    POSITIVE_GOD("🌟"), // 긍정 신 뱃지

    // 부정 감소 뱃지
    NEGATIVE_AWARE("🧠"), // 부정 인식 뱃지
    NEGATIVE_CONTROL("🕊️"), // 부정 조절 뱃지
    NEGATIVE_OVERCOME("🔥"), // 부정 극복 뱃지
    NEGATIVE_MASTER("🎯"), // 부정 마스터 뱃지
    NEGATIVE_PURIFY("💎"), // 완전 정화 뱃지

    // 기록 수 달성 뱃지
    ACTIVITY_START("🚀"), // 활동 시작 뱃지
    ACTIVITY_GROWTH("📈"), // 활동 증가 뱃지
    ACTIVITY_VIGOR("⚡"), // 활동 왕성 뱃지
    ACTIVITY_EXPERT("🏅"), // 활동 전문가 뱃지
    ACTIVITY_MANIA("🎖️"); // 활동 마니아 뱃지

    private final String emoji;
}
