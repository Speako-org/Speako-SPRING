package com.speako.domain.user.converter;

import com.speako.domain.auth.dto.reqDTO.SignupRequest;
import com.speako.domain.user.entity.User;
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
                .build();
    }
}
