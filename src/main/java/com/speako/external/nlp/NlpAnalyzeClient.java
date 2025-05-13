package com.speako.external.nlp;

public interface NlpAnalyzeClient {
    void analyze(Long transcriptionId, String transcriptionS3Path);
}
