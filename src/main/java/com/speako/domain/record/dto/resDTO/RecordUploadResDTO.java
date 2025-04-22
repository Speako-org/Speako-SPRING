package com.speako.domain.record.dto.resDTO;

// getPresignedUrl API 응답 DTO (생성된 record의 id도 포함)
public record RecordUploadResDTO(

        Long recordId,
        String presignedUrl,
        String key // S3에 업로드 될 '파일 경로 + 파일 고유 이름' (key 값)
) {
}
