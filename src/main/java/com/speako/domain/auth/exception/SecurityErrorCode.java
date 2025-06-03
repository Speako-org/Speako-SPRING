package com.speako.domain.auth.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements BaseErrorCode {

    // AUTH401-1은 CustomAuthenticationEntryPoint 파일에서 'security 필터 도중 인증 실패'시의 에러로 구현되어 있음
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401-2", "유효하지 않은 Refresh 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401-3", "유효하지 않은 Access 토큰입니다."),
    BLACKLISTED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401-4", "블랙리스트 처리된 토큰입니다."),
    JWT_AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "AUTH401-5", "JWT 인증 중 오류가 발생했습니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401-6", "이메일 또는 비밀번호가 올바르지 않습니다."),

    TOO_MANY_LOGIN_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "AUTH429-1", "로그인 시도 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
