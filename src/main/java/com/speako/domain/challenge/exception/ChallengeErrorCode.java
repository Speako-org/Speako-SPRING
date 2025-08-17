package com.speako.domain.challenge.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChallengeErrorCode implements BaseErrorCode {

    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE404-1", "해당 ID 또는 조건에 해당하는 챌린지를 찾을 수 없습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
