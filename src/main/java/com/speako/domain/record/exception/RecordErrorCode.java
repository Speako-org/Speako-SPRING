package com.speako.domain.record.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseErrorCode {

    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD404-1", "Id 값과 일치하는 Record가 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
