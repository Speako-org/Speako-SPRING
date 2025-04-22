package com.speako.domain.record.controller;

import com.speako.domain.record.dto.resDTO.RecordUploadResDTO;
import com.speako.domain.record.service.command.RecordCommandService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

//@Profile("s3")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordCommandService recordCommandService;

    @PostMapping("/presigned-url")
    @Operation(method = "POST", summary = "Presigned url 발급 API", description = "클라이언트가 S3에 파일을 직접 업로드 할 수 있도록 URL을 발급해주는 API입니다.")
    public CustomResponse<RecordUploadResDTO> getPresignedUrl(
            // @AuthUser or @CurrentUser 사용하여 유저 정보 받아오기
            @Parameter(description = "최초 요청일 시 null을, 재발급 요청일 시 이전에 반환받은 recordId를 넣어주세요.")
            @RequestParam(value = "recordId", required = false) Long recordId,

            @Parameter(description = "확장자를 포함한 전체 파일명을 넣어주세요. ex) audio_2025_04_17.wav")
            @RequestParam(value = "fileName") String fileName) {

        return CustomResponse.onSuccess(recordCommandService.createPresignedUrl(recordId, fileName));
    }

    @PostMapping("/{recordId}/transcriptions")
    @Operation(method = "POST", summary = "STT 처리 시작 API", description = "S3에 녹음파일 업로드 완료 후, 텍스트화 및 분석을 요청하는 API입니다.")
    public CustomResponse<String> completeUploadAndStartStt(
            // @AuthUser or @CurrentUser 사용하여 유저 정보 받아오기
            @PathVariable(value = "recordId") Long recordId,

            @Parameter(description = "버킷에 업로드된 녹음파일 객체의 디렉토리 포함 경로를 넣어주세요. ex) voice/transcribe-example.txt")
            @RequestParam(value = "recordS3Path") String recordS3Path,

            @Parameter(description = "ex) 2025-04-17T23:00:00.000000")
            @RequestParam(value = "startTime") LocalDateTime startTime,

            @Parameter(description = "ex) 2025-04-17T23:00:00.000000")
            @RequestParam(value = "endTime") LocalDateTime endTime) {

        recordCommandService.completeUploadAndStartStt(recordId, recordS3Path, startTime, endTime);
        return CustomResponse.onSuccess("녹음파일 업로드 완료 처리 및 STT 요청에 성공하였습니다.");
    }
}
