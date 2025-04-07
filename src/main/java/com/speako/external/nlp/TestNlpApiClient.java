package com.speako.external.nlp;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Profile("test")
@Component
public class TestNlpApiClient implements NlpAnalysisClient{
    @Override
    public NlpAnalysisResponse analyze(String text) {
        try {
            long delay = 5_000 + new Random().nextInt(6_000); // 5초 ~ 11초 랜덤
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new NlpAnalysisResponse(
                1L,
                List.of("넌 바보야"),
                List.of("바보"),
                0.6,
                0.3
        );
    }
}
