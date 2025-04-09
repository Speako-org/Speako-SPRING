package com.speako.domain.record.exception.handler;

import com.speako.global.apiPayload.code.BaseErrorCode;
import com.speako.global.apiPayload.exception.CustomException;

public class RecordHandler extends CustomException {
    public RecordHandler(BaseErrorCode code) {
        super(code);
    }
}
