package com.speako.domain.userinfo.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MonthlyStatErrorCode implements BaseErrorCode {

    MONTHLY_STAT__NOT_FOUND(HttpStatus.NOT_FOUND, "MONTHLY404-1", "조건에 해당하는 MonthlyStat가 존재하지 않습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
