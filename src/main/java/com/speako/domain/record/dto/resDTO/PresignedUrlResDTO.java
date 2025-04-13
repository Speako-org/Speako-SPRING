package com.speako.domain.record.dto.resDTO;

public record PresignedUrlResDTO(

        String presignedUrl,
        String key // S3에 업로드 될 '파일 경로 + 파일 고유 이름' (key 값)
) {
}
