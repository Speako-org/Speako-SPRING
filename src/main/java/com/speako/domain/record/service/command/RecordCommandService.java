package com.speako.domain.record.service.command;

import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.record.domain.Record;
import com.speako.domain.record.domain.enums.RecordStatus;
import com.speako.domain.record.dto.resDTO.PresignedUrlResDTO;
import com.speako.domain.record.dto.resDTO.RecordUploadResDTO;
import com.speako.domain.record.exception.RecordErrorCode;
import com.speako.domain.record.repository.RecordRepository;
import com.speako.domain.transcription.service.command.TranscriptionCommandService;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.repository.UserRepository;
import com.speako.external.aws.service.AwsS3Service;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordCommandService {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final AwsS3Service awsS3Service;
    private final TranscriptionCommandService transcriptionCommandService;

    // presigned url 발급 및 Record 엔티티 생성
    public RecordUploadResDTO createPresignedUrl(Long userId, Long recordId, String fileName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.USER_NOT_FOUND));

        PresignedUrlResDTO presignedDTO = awsS3Service.getPresignedUrl(fileName);

        if (recordId == null) {
            // recordId가 null이면, SAVING 상태의 새로운 Record 엔티티 생성
            Record record = recordRepository.save(
                    Record.builder()
                            .user(user)
                            .recordStatus(RecordStatus.SAVING)
                            .build()
            );
            recordId = record.getId();

        } else {
            // recordId가 null이 아니면, DB에 해당 Id의 값이 있는지 확인
            recordRepository.findById(recordId)
                    .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));
        }
        return new RecordUploadResDTO(recordId, presignedDTO.presignedUrl(), presignedDTO.key());
    }

    // 녹음파일 업로드 완료처리 및 STT 변환 시작
    public void completeUploadAndStartStt(Long userId, Long recordId, String recordS3Path, LocalDateTime startTime, LocalDateTime endTime) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.USER_NOT_FOUND));

        // Record 상태 및 s3 경로 업데이트
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));
        record.updateRecordStatus(RecordStatus.SAVED);
        record.updateRecordS3Path(recordS3Path);

        // 전달받은 메타데이터로 Transcription 생성 및 fastApi 호출 (Transcribe 작업 요청)
        transcriptionCommandService.startStt(user, record, startTime, endTime);
    }

    // Record soft 삭제 처리
    public void softDeleteRecord(Record record) {
        record.updateDeletedAt(LocalDateTime.now());
    }
}
