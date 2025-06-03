package com.speako.domain.auth.dto.resDTO;

// 클라이언트에게 응답으로 보내는 토큰 쌍을 의미
public record JwtResponse(

        String accessToken,
        String refreshToken
) {
}
