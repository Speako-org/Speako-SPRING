package com.speako.domain.challenge.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserBadgeErrorCode implements BaseErrorCode {

    USER_BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_BADGE404-1", "ID와 일치하는 사용자 뱃지를 찾을 수 없습니다."),

    USER_BADGE_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, "USER_BADGE403-1", "해당 사용자에게 속하지 않은 뱃지입니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
