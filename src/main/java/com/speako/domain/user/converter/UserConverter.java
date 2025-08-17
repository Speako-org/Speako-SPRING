package com.speako.domain.user.converter;

import com.speako.domain.auth.dto.reqDTO.SignupRequest;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.AuthProvider;
import com.speako.domain.user.domain.enums.ImageType;
import com.speako.domain.user.domain.enums.UserGender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserConverter {

    // SignupRequest -> User (password는 암호화하여 저장)
    public static User toUser(SignupRequest signupRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .email(signupRequest.email())
                .password(bCryptPasswordEncoder.encode(signupRequest.password()))
                .username(signupRequest.username())
                .age(signupRequest.age())
                .gender(signupRequest.gender())
                .imageType(ImageType.DEFAULT)
                .authProvider(AuthProvider.LOCAL)
                .build();
    }

    public static User kakaoToUser(String email, String username) {
        return User.builder()
                .email(email)
                .password(null) // 로그인 인증에 사용될 일 없음
                .username(username)
                .age(null)
                .gender(UserGender.OTHER)
                .imageType(ImageType.DEFAULT)
                .authProvider(AuthProvider.KAKAO)
                .build();
    }
}
