package com.speako.domain.analysis.controller;

import com.speako.domain.analysis.dto.resDTO.AnalysisResponseDTO;
import com.speako.domain.analysis.service.query.AnalysisQueryService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analyses")
public class AnalysisController {

    private final AnalysisQueryService analysisQueryService;

    @GetMapping("/{analysisId}")
    @Operation(method = "GET", summary = "분석 결과 조회 API", description = "녹음 기록의 분석 결과를 조회하는 API입니다.")
    public CustomResponse<AnalysisResponseDTO> getAnalysis(
            @PathVariable(value = "analysisId") Long analysisId) {

        AnalysisResponseDTO analysisResponseDTO = analysisQueryService.getAnalysis(analysisId);
        return CustomResponse.onSuccess(analysisResponseDTO);
    }
}
