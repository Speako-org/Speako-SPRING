package com.speako.domain.auth.dto.reqDTO;

// 로그인 API의 요청 DTO
public record LoginRequest(

        String email,
        String password
) {
}
