package com.speako.domain.analysis.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.transcription.domain.enums.TranscriptionStatus;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.external.aws.service.AwsS3Service;
import com.speako.global.config.AwsS3Config;
import com.speako.global.config.WebClientConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static com.speako.domain.transcription.domain.enums.TranscriptionStatus.ANALYSIS_COMPLETED;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnalysisCommandServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TranscriptionRepository transcriptionRepository;

    @Autowired
    private AnalysisRepository analysisRepository;


    @MockBean
    private AwsS3Service awsS3Service;
    @MockBean
    private AwsS3Config awsS3Config;
    @MockBean
    private WebClient webClient;
    @MockBean
    private WebClientConfig webClientConfig;

    @Test
    void 분석_콜백_정상처리_흐름() throws Exception {
        // given: transcription 미리 저장
        Transcription transcription = transcriptionRepository.save(createTranscription());

        String payload = """
        {
            "transcription_id": %d,
            "data": {
                "negative_words": ["바보"],
                "negative_ratio": 0.2,
                "positive_ratio": 0.8
            }
        }
        """.formatted(transcription.getId());

        // when
        //callback
        mockMvc.perform(post("/api/analyze/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        // then
        Transcription updated = transcriptionRepository.findById(transcription.getId()).get();
        assertEquals(ANALYSIS_COMPLETED, updated.getTranscriptionStatus());

        List<Analysis> saved = analysisRepository.findAll();
        assertFalse(saved.isEmpty());

    }

    private Transcription createTranscription() {
        return Transcription.builder()
                .title("test")
                .s3Path("test.s3")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(1))
                .transcriptionStatus(TranscriptionStatus.STT_COMPLETED)
                .build();
    }
}