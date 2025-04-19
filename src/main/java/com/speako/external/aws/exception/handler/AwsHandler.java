package com.speako.external.aws.exception.handler;

import com.speako.global.apiPayload.code.BaseErrorCode;
import com.speako.global.apiPayload.exception.CustomException;

public class AwsHandler extends CustomException {
    public AwsHandler(BaseErrorCode code) {
        super(code);
    }
}
