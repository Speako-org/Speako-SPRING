package com.speako.domain.userinfo.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserAchievementErrorCode implements BaseErrorCode {

    USER_ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACHV404-1", "현재 유저에 해당하는 UserAchievement가 존재하지 않습니다."),

    USER_ACHIEVEMENT_BADGE_TOTAL_ZERO(HttpStatus.CONFLICT, "ACHV409-1", "UserAchievement의 총 UserBadge 수는 0보다 큰 값이어야 합니다. (미획득 포함)"),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
