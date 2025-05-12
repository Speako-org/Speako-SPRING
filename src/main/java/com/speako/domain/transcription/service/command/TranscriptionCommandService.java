package com.speako.domain.transcription.service.command;

import com.speako.domain.record.entity.Record;
import com.speako.domain.transcription.dto.reqDTO.TranscribeReqDTO;
import com.speako.domain.transcription.entity.Transcription;
import com.speako.domain.transcription.entity.enums.TranscriptionStatus;
import com.speako.domain.transcription.exception.code.TranscriptionErrorCode;
import com.speako.domain.transcription.exception.handler.TranscriptionHandler;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.event.stt.SttCompletedEvent;
import com.speako.external.aws.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TranscriptionCommandService {

    private final TranscriptionRepository transcriptionRepository;
    private final WebClient fastApiWebClient;
    private final ApplicationEventPublisher eventPublisher;
    private final AwsS3Service awsS3Service;

    // 전달받은 메타데이터로 Transcription 생성 및 fastApi 호출 (Transcribe 작업 요청)
    public void startStt(Record record, LocalDateTime startTime, LocalDateTime endTime) {

        // 기본 title 값 생성  ex) 기록_250417_135703
        String defaultTitle = "기록_" + startTime.format(DateTimeFormatter.ofPattern("yyMMdd_HHmmss"));

        // 메타데이터로 Transcription 엔티티 생성 및 저장
        Transcription transcription = transcriptionRepository.save(
                Transcription.builder()
//                        .user(user)
                        .record(record)
                        .title(defaultTitle)
                        .startTime(startTime)
                        .endTime(endTime)
                        .transcriptionStatus(TranscriptionStatus.STT_PENDING)
                        .build()
        );
        callFastApiForStt(transcription.getId(), record.getS3Path());
    }

    // fastApi 호출 (Transcribe 작업 요청)
    private void callFastApiForStt(Long transcriptionId, String recordS3Path) {

        TranscribeReqDTO transcribeReqDTO = new TranscribeReqDTO(transcriptionId, recordS3Path);
        // fastApi 비동기적 호출
        fastApiWebClient
                .post()
                .uri("/transcribe/start")
                .bodyValue(transcribeReqDTO)
                .retrieve()
                .toBodilessEntity() // fastApi 응답 받을 때, 응답 본문은 무시하고 상태 코드만 체크
                .subscribe(
                        response -> {
                            log.info("fastApi 호출 성공. 응답 status = {}", response.getStatusCode());
                            // fastApi 호출 성공 시 상태 업데이트 (별도 스레드에서 처리)
                            Schedulers.boundedElastic().schedule(() -> {
                                transcriptionRepository.findById(transcriptionId)
                                        .ifPresent(transcription -> {
                                            transcription.updateTranscriptionStatus(TranscriptionStatus.STT_IN_PROGRESS);
                                            transcriptionRepository.save(transcription); // 개별 스레드이므로 @Transactional 적용 안 될 가능성 매우 높음
                                        });
                            });
                        },
                        error -> {
                            log.error("fastApi 호출 실패. error message = {}", error.getMessage());
                            // fastApi 호출 실패 시 상태 업데이트 (별도 스레드에서 처리)
                            Schedulers.boundedElastic().schedule(() -> {
                                transcriptionRepository.findById(transcriptionId)
                                        .ifPresent(transcription -> {
                                            transcription.updateTranscriptionStatus(TranscriptionStatus.STT_FAILED);
                                            transcriptionRepository.save(transcription);
                                        });
                            });
                        }
                );
    }

    // Transcribe 작업 완료처리 및 SttCompletedEvent 발행
    public void completeStt(Long transcriptionId, String transcriptionS3Path) {

        Transcription transcription = transcriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new TranscriptionHandler(TranscriptionErrorCode.TRANSCRIPTION_NOT_FOUND));

        // S3 버킷에서 전체 텍스트 가져오기
        String transcriptionFullText = awsS3Service.getTranscriptionFullText(transcriptionS3Path);
        log.info("버킷 속 전체 텍스트: {}", transcriptionFullText);

        // 전체 텍스트에서 썸네일 텍스트 분리 (앞 100자)
        String thumbnailText = extractThumbnailText(transcriptionFullText, 100);

        // transcription 엔티티 업데이트
        transcription.updateTranscriptionS3Path(transcriptionS3Path);
        transcription.updateThumbnailText(thumbnailText);
        transcription.updateTranscriptionStatus(TranscriptionStatus.STT_COMPLETED);

        // SttCompletedEvent 발행
        eventPublisher.publishEvent(new SttCompletedEvent(transcriptionId, transcriptionFullText));
    }

    /*
        전체 텍스트에서 썸네일 텍스트 분리
        - ./!/? 뒤에 오는 공백을 기준으로 문장 분리
        - 문장 분리 후 텍스트 길이가 100자 초과 시 분리 종료 (미초과 시 한 문장 더)
     */
    private String extractThumbnailText(String fullText, int minLength) {

        // Text 파일이 빈 파일인 경우 빈 문자열 반환
        if (fullText == null || fullText.isBlank()) {
            return "";
        }
        // 줄바꿈/탭 등을 제거하고 공백으로 대체, 문자열 양끝의 공백 제거 (줄바꿈이나 탭이나 양끝의 공백이 있는 경우는 없을 것 같지만, 혹시 모르니까...)
        String flattenedText = fullText.replaceAll("[\\n\\r\\t]+", " ").trim();

        /*
            ./!/? 뒤에 공백이 있는 경우, 그 공백을 기준으로 잘라서 문장 분리
            - (?<=...) : 긍정형 후방탐색(특정 문자 앞에 오는 것을 출력)
            - \\s+ : 공백 문자 하나 이상
         */
        String[] sentences = flattenedText.split("(?<=[.!?])\\s+");

        // mutable한 StringBuilder 객체를 생성하여, 100자 초과 시까지 문장 연결
        StringBuilder thumbnailBuilder = new StringBuilder();
        for (String sentence : sentences) {
            thumbnailBuilder.append(sentence).append(" ");
            if (thumbnailBuilder.length() > minLength) {
                break;
            }
        }
        return thumbnailBuilder.toString().trim();
    }
}

