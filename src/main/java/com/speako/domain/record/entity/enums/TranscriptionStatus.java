package com.speako.domain.record.entity.enums;

public enum TranscriptionStatus {

    STT_IN_PROGRESS, // 녹음을 텍스트로 변환하는 중
    STT_COMPLETED, // 텍스트 변환 완료
    ANALYSIS_IN_PROGRESS, // 분석/통계 진행 중
    ANALYSIS_COMPLETED, // 분석/통계 완료
    ANALYSIS_FAIL // 분석/통계 실패
}
