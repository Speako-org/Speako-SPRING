package com.speako.external.aws.exception;

import com.speako.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AwsErrorCode implements BaseErrorCode {

    TEXT_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AWS500-1", "S3 텍스트 파일 읽기에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
