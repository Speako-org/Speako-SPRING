package com.speako.domain.transcription.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TranscriptionErrorCode implements BaseErrorCode {

    TRANSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "TRANSCRIPTION404-1", "Id 값과 일치하는 Transcription이 존재하지 않습니다."),

    ANALYSIS_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "TRANSCRIPTION400-1", "해당 기록의 분석이 아직 완료되지 않았습니다."),

    TRANSCRIPTION_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "TRANSCRIPTION403-1", "해당 녹음 기록을 수정할 권한이 없습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
