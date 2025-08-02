package com.speako.domain.analysis.controller;

import com.speako.domain.analysis.dto.reqDTO.NlpCallbackRequestDTO;
import com.speako.domain.analysis.dto.resDTO.AnalysisResponseDTO;
import com.speako.domain.analysis.service.command.AnalysisCommandService;
import com.speako.domain.analysis.service.query.AnalysisQueryService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisQueryService analysisQueryService;
    private final AnalysisCommandService analysisCommandService;

    @GetMapping("/transcription/{transcriptionId}/analyses")
    @Operation(method = "GET", summary = "분석 결과 조회 API", description = "녹음 기록의 분석 결과를 조회하는 API입니다.")
    public CustomResponse<AnalysisResponseDTO> getAnalysis(
            @PathVariable(value = "transcriptionId") Long transcriptionId) {

        AnalysisResponseDTO analysisResponseDTO = analysisQueryService.getAnalysis(transcriptionId);
        return CustomResponse.onSuccess(analysisResponseDTO);
    }

    @PostMapping("/analyze/complete")
    @Operation(summary = "NLP 분석 결과 콜백", description = "NLP 서버에서 분석 결과를 전송하는 콜백 API입니다.")
    public CustomResponse<String> handleAnalysisCallback(
            @RequestBody NlpCallbackRequestDTO nlpCallbackRequestDTO) {

        //TODO: 분석 실패시 처리
        analysisCommandService.handleAnalysisCallback(nlpCallbackRequestDTO.transcriptionId(), nlpCallbackRequestDTO.data());
        //TODO: 성공 메세지 수정
        return CustomResponse.onSuccess("NLP 분석 완료");
    }
}
