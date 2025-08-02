package com.speako.domain.auth.dto.reqDTO;

import com.speako.domain.user.domain.enums.UserGender;

// 회원가입 API의 요청 DTO
public record SignupRequest(

        String email,
        String password,
        String username,
        Integer age,
        UserGender gender
) {
}
