package com.speako.domain.transcription.controller;

import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.transcription.dto.reqDTO.UpdateTranscriptionTitleReqDTO;
import com.speako.domain.transcription.dto.resDTO.TranscriptionResDTO;
import com.speako.domain.transcription.dto.resDTO.UpdateTranscriptionTitleResDTO;
import com.speako.domain.transcription.service.command.TranscriptionCommandService;
import com.speako.domain.transcription.service.query.TranscriptionQueryService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transcriptions")
public class TranscriptionController {

    private final TranscriptionCommandService transcriptionCommandService;
    private final TranscriptionQueryService transcriptionQueryService;

    @PostMapping("/{transcriptionId}/complete")
    @Operation(method = "POST", summary = "STT 완료처리 및 NLP 분석 시작 API", description = "STT 메타데이터를 저장하고, 이벤트 발행을 통해 NLP 분석을 시작하도록 하는 API입니다.")
    public CustomResponse<String> completeStt(
            @PathVariable Long transcriptionId,

            @Parameter(description = "버킷에 업로드된 텍스트파일 객체의 디렉토리 포함 경로를 넣어주세요. ex) result-text/transcribe-example.txt")
            @RequestParam(value = "transcriptionS3Path") String transcriptionS3Path) {

        transcriptionCommandService.completeStt(transcriptionId, transcriptionS3Path);
        return CustomResponse.onSuccess("Transcribe 완료 처리 및 NLP 분석 요청에 성공하였습니다.");
    }

    @GetMapping("/")
    @Operation(method = "GET", summary = "녹음 기록 리스트 조회 API", description = "특정 날짜에 해당하는 녹음 기록 리스트를 조회하는 API입니다.")
    public CustomResponse<List<TranscriptionResDTO>> getTranscriptionListByDate(
            @LoginUser CustomUserDetails customUserDetails,

            @Parameter(description = "녹음기록을 조회할 날짜를 입력해주세요. ex) 2025-06-20")
            @RequestParam(value = "date") LocalDate date) {

        return CustomResponse.onSuccess(transcriptionQueryService.getTranscriptionListByDate(customUserDetails, date));
    }

    @PatchMapping("/{transcriptionId}/title")
    @Operation(method = "PATCH", summary = "녹음기록 title update API", description = "특정 녹음기록의 title을 변경하는 API입니다.")
    public CustomResponse<UpdateTranscriptionTitleResDTO> updateTranscriptionTitle(
            @LoginUser CustomUserDetails userDetails,
            @PathVariable Long transcriptionId,
            @Valid @RequestBody UpdateTranscriptionTitleReqDTO updateTranscriptionTitleDTO) {

        UpdateTranscriptionTitleResDTO resDTO = transcriptionCommandService.updateTranscriptionTitle(
                userDetails.getId(),
                transcriptionId,
                updateTranscriptionTitleDTO.newTranscriptionTitle()
        );
        return CustomResponse.onSuccess(resDTO);
    }

    // 녹음기록 softDelete API 구현
    @DeleteMapping("/{transcriptionId}")
    @Operation(method = "DELETE", summary = "녹음기록 삭제 API", description = "녹음기록과 연관된 모든 기록을 삭제하는 API입니다.")
    public CustomResponse<String> softDeleteTranscription(
            @LoginUser CustomUserDetails userDetails,
            @PathVariable Long transcriptionId) {

        transcriptionCommandService.softDeleteTranscription(userDetails.getId(), transcriptionId);
        return CustomResponse.onSuccess(transcriptionId + "번 녹음기록과 관련된 기록이 성공적으로 삭제되었습니다.");
    }
}
