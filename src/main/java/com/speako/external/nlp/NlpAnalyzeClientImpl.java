package com.speako.external.nlp;

import com.speako.external.nlp.reqDTO.AnalyzeReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class NlpAnalyzeClientImpl implements NlpAnalyzeClient{

    private final WebClient fastApiWebClient;

    @Override
    public void analyze(Long transcriptionId, String transcriptionS3Path) {

        AnalyzeReqDTO analyzeReqDTO = new AnalyzeReqDTO(transcriptionId, transcriptionS3Path);

        fastApiWebClient.post()
                .uri("/analyze/start")
                .bodyValue(analyzeReqDTO)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        unused -> log.info("[NLP 요청 성공] transcriptionId={}", transcriptionId),
                        error -> log.error("[NLP 요청 실패] transcriptionId={} / message={}", transcriptionId, error.getMessage(), error)
                );
    }
}
