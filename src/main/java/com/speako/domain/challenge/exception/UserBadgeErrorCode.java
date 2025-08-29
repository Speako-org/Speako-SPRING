package com.speako.domain.challenge.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserBadgeErrorCode implements BaseErrorCode {

    USER_BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_BADGE404-1", "사용자가 획득한 뱃지 중, 해당 ID와 일치하는 뱃지를 찾을 수 없습니다."),

    DUPLICATE_MAIN_BADGE_FOUND(HttpStatus.CONFLICT, "USER_BADGE409-1", "사용자에게 대표 뱃지가 2개 이상 설정되어 있습니다."),
    ALREADY_POSTED_BADGE(HttpStatus.CONFLICT, "USER_BADGE409-2", "해당 뱃지는 이미 게시글로 등록되어 있습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
