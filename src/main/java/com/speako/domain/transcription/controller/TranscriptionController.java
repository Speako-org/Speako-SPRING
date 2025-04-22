package com.speako.domain.transcription.controller;

import com.speako.domain.transcription.service.command.TranscriptionCommandService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transcriptions")
public class TranscriptionController {

    private final TranscriptionCommandService transcriptionCommandService;

    @PostMapping("/{transcriptionId}/complete")
    @Operation(method = "POST", summary = "STT 완료처리 및 NLP 분석 시작 API", description = "STT 메타데이터를 저장하고, 이벤트 발행을 통해 NLP 분석을 시작하도록 하는 API입니다.")
    public CustomResponse<String> completeStt(
            @PathVariable Long transcriptionId,

            @Parameter(description = "버킷에 업로드된 텍스트파일 객체의 디렉토리 포함 경로를 넣어주세요. ex) result-text/transcribe-example.txt")
            @RequestParam(value = "transcriptionS3Path") String transcriptionS3Path) {

        transcriptionCommandService.completeStt(transcriptionId, transcriptionS3Path);
        return CustomResponse.onSuccess("Transcribe 완료 처리 및 NLP 분석 요청에 성공하였습니다.");
    }
}
