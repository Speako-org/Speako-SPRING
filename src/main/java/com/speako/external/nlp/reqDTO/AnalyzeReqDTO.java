package com.speako.external.nlp.reqDTO;

public record AnalyzeReqDTO(

        Long transcriptionId,
        String transcriptionS3Path
) {
}
