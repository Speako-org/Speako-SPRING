package com.speako.domain.security.jwt;

// 내부에서 전달되는 토큰 쌍을 의미
public record JwtToken(

        String accessToken,
        String refreshToken
) {
}
