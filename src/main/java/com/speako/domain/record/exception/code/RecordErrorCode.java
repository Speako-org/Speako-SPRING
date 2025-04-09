package com.speako.domain.record.exception.code;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseErrorCode {

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
