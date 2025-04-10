package com.speako.external.nlp;

import org.springframework.stereotype.Component;

@Component
public class NlpApiClient implements NlpAnalysisClient {

    @Override
    public NlpAnalysisResponse analyze(String text) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
