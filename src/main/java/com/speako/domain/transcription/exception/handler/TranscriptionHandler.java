package com.speako.domain.transcription.exception.handler;

import com.speako.global.apiPayload.code.BaseErrorCode;
import com.speako.global.apiPayload.exception.CustomException;

public class TranscriptionHandler extends CustomException {
    public TranscriptionHandler(BaseErrorCode code) {
        super(code);
    }
}
