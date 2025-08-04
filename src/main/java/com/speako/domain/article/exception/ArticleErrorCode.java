package com.speako.domain.article.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor

public enum ArticleErrorCode implements BaseErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE404-1", "사용자를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.NOT_FOUND, "ARTICLE404-2", "해당 뱃지를 찾을 수 없습니다."),
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE404-3", "해당 글을 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ARTICLE403-1", "삭제는 글을 쓴 작성자만 할 수 있습니다."),
    ALREADY_LIKED(HttpStatus.FORBIDDEN, "ARTICLE403-2", "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE404-4", "좋아요를 누른 적이 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
