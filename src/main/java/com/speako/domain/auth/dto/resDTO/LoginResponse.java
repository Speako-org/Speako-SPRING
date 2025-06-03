package com.speako.domain.auth.dto.resDTO;

// 로그인 API의 응답 DTO
public record LoginResponse(

        Long userId,
        String accessToken,
        String refreshToken
) {
}
