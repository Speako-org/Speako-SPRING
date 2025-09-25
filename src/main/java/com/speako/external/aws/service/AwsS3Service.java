package com.speako.external.aws.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.speako.domain.record.dto.resDTO.PresignedUrlResDTO;
import com.speako.external.aws.exception.AwsErrorCode;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

//@Profile("s3")
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // Presigned url 생성 및 반환
    public PresignedUrlResDTO getPresignedPostUrl(String fileName) {

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

    // Presigned url(GET) 생성 및 반환
    public String getPresignedGetUrl(String key) {

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(getExpiration());

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toExternalForm();
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

    // S3 버킷에서 전체 텍스트 가져오기
    public String getTranscriptionFullText(String transcriptionS3Path) {

        // transcriptionS3Path이 result-text/transcribe-example.txt 와 같이 디렉토리도 포함한 형식으로 되어있다고 가정
        S3Object s3Object = amazonS3.getObject(bucket, transcriptionS3Path);

        try (S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
            // 스트림 전체를 한 번에 문자열로 변환
            return new String(s3ObjectInputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CustomException(AwsErrorCode.TEXT_READ_FAILED);
        }
    }
}
