package com.speako.domain.transcription.service.query;

import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.transcription.converter.TranscriptionConverter;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.transcription.dto.resDTO.TranscriptionResDTO;
import com.speako.domain.transcription.repository.TranscriptionRepository;
import com.speako.external.aws.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TranscriptionQueryService {

    private final TranscriptionRepository transcriptionRepository;
    private final AwsS3Service awsS3Service;

    // 특정 날짜의 녹음 기록 리스트 조회
    public List<TranscriptionResDTO> getTranscriptionListByDate(CustomUserDetails customUserDetails, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Transcription> transcriptions = transcriptionRepository.findAllByUserIdAndCreatedAtBetweenAndDeletedAtIsNullOrderByCreatedAtAsc(
                customUserDetails.getId(), start, end
        );
        return transcriptions.stream()
                .map(t -> {
                    String url = awsS3Service.getPresignedGetUrl(t.getRecord().getS3Path());
                    return TranscriptionConverter.toTranscriptionResDTO(t, url);
                })
                .toList();
    }
}
