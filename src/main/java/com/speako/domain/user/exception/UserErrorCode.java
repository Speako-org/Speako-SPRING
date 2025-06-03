package com.speako.domain.user.exception;


import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "USER404-1", "해당 이메일에 해당하는 사용자가 존재하지 않습니다."),

    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409-1", "이미 가입된 이메일입니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
