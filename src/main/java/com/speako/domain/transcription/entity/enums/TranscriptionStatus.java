package com.speako.domain.transcription.entity.enums;

public enum TranscriptionStatus {

    STT_PENDING, // 텍스트 변환 요청 대기 중
    STT_IN_PROGRESS, // 녹음을 텍스트로 변환하는 중
    STT_COMPLETED, // 텍스트 변환 완료
    STT_FAILED, // 텍스트 변환 실패

    ANALYSIS_IN_PROGRESS, // 분석/통계 진행 중
    ANALYSIS_COMPLETED, // 분석/통계 완료
    ANALYSIS_FAIL // 분석/통계 실패
}
