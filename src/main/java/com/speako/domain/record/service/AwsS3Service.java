package com.speako.domain.record.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.speako.domain.record.dto.resDTO.PresignedUrlResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

//@Profile("s3")
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // Presigned url 생성 및 반환
    public PresignedUrlResDTO getPresignedUrl(String fileName) {

        // 파일 확장자 추출
        String ext = fileName.substring(fileName.lastIndexOf("."));
        // 확장자가 정해진 형식(아직 안정해짐)이 아니면 백엔드 측에서도 에러 발생시키기 (프론트에서도 당연히 1차로 거르기)

        // 고유 UUID 사용하여 파일 이름 설정
        String key = "voice/" + UUID.randomUUID() + ext;

        // Presigned url 생성하기
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(key);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return new PresignedUrlResDTO(url.toExternalForm(), key);
    }

    // Presigned URL 생성 요청 객체 생성
    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String key) {

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT) // PUT: 업로드 용
                        .withExpiration(getExpiration());

        return generatePresignedUrlRequest;
    }

    // 현재 시간 기준 expire 시각 설정
    private static Date getExpiration() {

        Date expiration = new Date();
        long expirationTime = expiration.getTime() + 1000 * 60 * 3; // 3분 이후
        expiration.setTime(expirationTime);

        return expiration;
    }
}
