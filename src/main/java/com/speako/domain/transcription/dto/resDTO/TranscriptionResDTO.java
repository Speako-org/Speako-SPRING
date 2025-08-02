package com.speako.domain.transcription.dto.resDTO;

import com.speako.domain.transcription.domain.enums.TranscriptionStatus;

import java.time.LocalDateTime;

// 녹음 기록 리스트 조회 시 응답값으로 전달되는 DTO
public record TranscriptionResDTO(

        Long transcriptionId,
        Long userId,
        Long recordId,
        String title,
        LocalDateTime startTime, // 녹음 시작 시간
        LocalDateTime endTime, // 녹음 종료 시간
        TranscriptionStatus transcriptionStatus, // 현재 해당 녹음 기록의 상태 ex) stt 변환중, nlp 분석중 ETC.
        LocalDateTime createdAt // 녹음 기록 생성 시간(endTime과 거의 같음)
) {
}
