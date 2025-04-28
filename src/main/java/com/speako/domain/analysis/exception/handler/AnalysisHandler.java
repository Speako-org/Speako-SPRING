package com.speako.domain.analysis.exception.handler;

import com.speako.global.apiPayload.code.BaseErrorCode;
import com.speako.global.apiPayload.exception.CustomException;

public class AnalysisHandler extends CustomException {
    public AnalysisHandler(BaseErrorCode code) {
        super(code);
    }
}
