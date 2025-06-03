package com.speako.domain.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jwt")
// yml 파일에서 JWT 설정 정보를 바인딩 받는 레코드 클래스
public record JwtProperties(

        String secretKey,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}
