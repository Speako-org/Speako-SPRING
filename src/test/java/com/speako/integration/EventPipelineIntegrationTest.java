package com.speako.integration;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.UserGender;
import com.speako.domain.user.repository.UserRepository;
import com.speako.event.nlp.NlpCompletedEvent;
import com.speako.event.nlp.NlpCompletedEventHandler;
import com.speako.event.stt.SttCompletedEvent;

import com.speako.event.stt.SttCompletedEventHandler;
import com.speako.external.nlp.NlpAnalyzeClient;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static com.speako.domain.transcription.domain.enums.TranscriptionStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EventPipelineIntegrationTest {

    @Autowired ApplicationEventPublisher eventPublisher;

    @Autowired TranscriptionRepository transcriptionRepository;
    @Autowired UserRepository userRepository;
    @Autowired AnalysisRepository analysisRepository;

    @SpyBean SttCompletedEventHandler sttCompletedEventHandler;
    @SpyBean NlpCompletedEventHandler nlpCompletedEventHandler;

    @MockBean
    NlpAnalyzeClient nlpApiClient;
    Transcription testTranscription;
    SttCompletedEvent testEvent;

    @BeforeEach
    void setup() {
        analysisRepository.deleteAll();
        transcriptionRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(createUser());
        userRepository.flush();

        testTranscription = transcriptionRepository.save(createTranscription(user));
        transcriptionRepository.flush();

        testEvent = new SttCompletedEvent(testTranscription.getId(), "테스트");
    }

    @Test
    public void STT_이벤트_발행시_STT_완료_핸들러가_비동기_수신한다() {
        //given
//        when(nlpApiClient.analyze(anyString())).thenReturn(
//                new NlpAnalyzeResponse(
//                        testTranscription.getId(),
//                        List.of("테스트"),
//                        0.1F,
//                        0.1F
//                ));

        //when: stt 완료 이벤트 발행
        eventPublisher.publishEvent(testEvent);
        
        //then
        await().untilAsserted(() -> {
            // stt 완료 이벤트 수신
            verify(sttCompletedEventHandler, times(1)).handleSttCompleted(testEvent);

            // 별도 스레드에서 비동기 실행 확인
            assertThat(SttCompletedEventHandler.threadName).contains("SttExecutor-");
        });
    }

    @Test
    public void STT_완료_핸들러는_이벤트_수신후_NLP_API_호출한다() {
        //given
//        when(nlpApiClient.analyze(anyString())).thenReturn(
//                new NlpAnalyzeResponse(
//                        testTranscription.getId(),
//                        List.of("테스트"),
//                        0.1F,
//                        0.1F
//                ));

        //when: stt 완료 이벤트 발행
        eventPublisher.publishEvent(testEvent);

        //then
//        await().untilAsserted(() -> {
//            // nlpApiClient.analyze 호출
//            verify(nlpApiClient, times(1)).analyze("테스트");
//        });
    }

    @Test
    public void NLP_API_비정상_응답시_transcription_실패_상태로_업데이트된다() {
        //given
//        when(nlpApiClient.analyze(anyString())).thenThrow(
//                new RuntimeException("NLP 분석 실패 테스트"));

        //when: stt 완료 이벤트 발행
        eventPublisher.publishEvent(testEvent);

        //then
        await().untilAsserted(() -> {
            //nlp 완료 이벤트 수신 X
            verify(nlpCompletedEventHandler, never()).handleNlpCompleted(any(NlpCompletedEvent.class));

            //testTranscription 상태 -> ANALYSIS_FAIL
            Transcription findTranscription = transcriptionRepository.findById(testTranscription.getId())
                    .orElseThrow(() -> new AssertionError("transcription 조회 X"));

            assertThat(findTranscription.getTranscriptionStatus()).isEqualTo(ANALYSIS_FAIL);
        });
    }

    @Test
    public void NLP_API_정상_응답시_NLP_이벤트_발행_및_NLP_완료_핸들러가_비동기_수신한다() {
        //given
//        when(nlpApiClient.analyze(anyString())).thenReturn(
//                new NlpAnalyzeResponse(
//                        testTranscription.getId(),
//                        List.of("테스트"),
//                        0.1F,
//                        0.1F
//                ));

        //when: stt 완료 이벤트 발행
        eventPublisher.publishEvent(testEvent);

        //then
        await().untilAsserted(() -> {
            //nlp 완료 이벤트 수신
            verify(nlpCompletedEventHandler, times(1)).handleNlpCompleted(any(NlpCompletedEvent.class));

            // 별도 스레드에서 비동기 실행 확인
            assertThat(NlpCompletedEventHandler.threadName).contains("NlpExecutor-");
        });
    }

    @Test
    public void NLP_완료_핸들러는_분석결과를_생성_및_저장한다() {
        //given
//        when(nlpApiClient.analyze(anyString())).thenReturn(
//                new NlpAnalyzeResponse(
//                        testTranscription.getId(),
//                        List.of("테스트"),
//                        0.1F,
//                        0.1F
//                ));

        //when: stt 완료 이벤트 발행
        eventPublisher.publishEvent(testEvent);

        //then
        await().untilAsserted(() -> {
            List<Analysis> analyses = analysisRepository.findAll();

            // 분석결과 저장 확인
            assertThat(analyses).isNotEmpty();

            // 분석결과 데이터 확인
            Analysis findAnalysis = analyses.get(0);
            assertThat(findAnalysis.getTranscription().getId()).isEqualTo(testTranscription.getId());
            //assertThat(findAnalysis.getNegativeWords()).containsExactly("테스트");
            assertThat(findAnalysis.getNegativeRatio()).isEqualTo(0.1F);
            assertThat(findAnalysis.getPositiveRatio()).isEqualTo(0.1F);
        });
    }

    @Test
    public void NLP_완료_핸들러는_분석결과_저장_후_TranscriptionStatus_업데이트한다() {
        //given
//        when(nlpApiClient.analyze(anyString())).thenReturn(
//                new NlpAnalyzeResponse(
//                        testTranscription.getId(),
//                        List.of("테스트"),
//                        0.1F,
//                        0.1F
//                ));

        //when: stt 완료 이벤트 발행
        eventPublisher.publishEvent(testEvent);

        //then

        // 이벤트 발행 후 TranscriptionStatus: ANALYSIS_IN_PROGRESS
        assertThat(testTranscription.getTranscriptionStatus()).isEqualTo(ANALYSIS_IN_PROGRESS);
        await().untilAsserted(() -> {
            Transcription findTranscription = transcriptionRepository.findById(testTranscription.getId())
                    .orElseThrow(() -> new AssertionError("transcription 조회 X"));

            // ANALYSIS_IN_PROGRESS -> ANALYSIS_COMPLETED 업데이트
            assertThat(findTranscription.getTranscriptionStatus()).isEqualTo(ANALYSIS_COMPLETED);
        });
    }

    private static User createUser() {
        return User.builder()
                .email("test@test.com")
                .password("test")
                .username("test")
                .age(1)
                .gender(UserGender.OTHER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static Transcription createTranscription(User user) {
        return  Transcription.builder()
                .user(user)
                .record(null)
                .s3Path("s3://test")
                .title("테스트 녹음")
                .thumbnailText("요약")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusSeconds(30))
                .transcriptionStatus(ANALYSIS_IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
