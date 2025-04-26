package com.speako.domain.analysis.exception.code;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisErrorCode implements BaseErrorCode {

    ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS404-1", "Id 값과 일치하는 Analysis가 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
