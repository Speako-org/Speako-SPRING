package com.speako.domain.record.controller;

import com.speako.domain.record.dto.resDTO.PresignedUrlResDTO;
import com.speako.domain.record.service.AwsS3Service;
import com.speako.domain.record.service.command.RecordCommandService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
public class RecordController {

    private final AwsS3Service awsS3Service;
    private final RecordCommandService recordCommandService;

    @PostMapping("/presigned-url")
    @Operation(method = "POST", summary = "Presigned url 발급 API", description = "클라이언트가 S3에 파일을 직접 업로드 할 수 있도록 URL을 발급해주는 API입니다.\n 확장자를 포함한 전체 파일명을 파라미터에 넣어주세요.")
    public CustomResponse<PresignedUrlResDTO> getPresignedUrl(
            @RequestParam(value = "fileName") String fileName) {

        return CustomResponse.onSuccess(awsS3Service.getPresignedUrl(fileName));
    }

    @PostMapping("/complete")
    @Operation(method = "POST", summary = "업로드 완료 API", description = "업로드 완료 후 호출하는 API입니다. DB에 파일 정보를 저장합니다.")
    public CustomResponse<String> completeUpload(
            // @AuthUser or @CurrentUser 사용하여 유저 정보 받아오기
            @RequestParam(value = "s3Path") String s3Path) {

        recordCommandService.completeUpload(s3Path);
        return CustomResponse.onSuccess("업로드 완료 처리되었습니다.");
    }
}
