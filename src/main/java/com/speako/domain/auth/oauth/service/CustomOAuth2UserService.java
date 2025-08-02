package com.speako.domain.auth.oauth.service;

import com.speako.domain.auth.oauth.userinfo.CustomOAuth2User;
import com.speako.domain.auth.oauth.userinfo.KakaoResponse;
import com.speako.domain.auth.oauth.userinfo.OAuth2Response;
import com.speako.domain.user.converter.UserConverter;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.AuthProvider;
import com.speako.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 카카오 API에서 사용자 정보 가져오기
        log.info("[CustomOAuth2UserService] OAuth2UserRequest에서 사용자 정보 가져오기 시작");
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // OAuth2 제공자가 kakao인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            log.info("[CustomOAuth2UserService] OAuth2 제공자는 kakao여야 함. 현재 제공자 : {}", registrationId);
            throw new OAuth2AuthenticationException("카카오 로그인을 통해서만 로그인 가능합니다.");
        }
        // attribute 하위 값들로 KakaoResponse 생성 후, 그 속의 email로 유저 조회
        OAuth2Response oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        Optional<User> localUser = userRepository.findByEmail(oAuth2Response.getEmail());
        log.info("[CustomOAuth2UserService] 카카오 응답 속 email로 검색된 localUser 존재 여부 : {}", localUser.isPresent());

        if (localUser.isPresent()) {
            User user = localUser.get();
            if (user.getAuthProvider() == AuthProvider.KAKAO) {
                // DB에 해당 이메일의 카카오 계정이 있으면, 정보 반환
                log.info("[CustomOAuth2UserService] 로그인 전적이 있는 카카오 계정입니다. DB에 저장된 유저 정보를 반환합니다.");
                return new CustomOAuth2User(user);
            } else {
                // DB에 해당 이메일의 유저 정보는 존재하나, 카카오 계정이 아니라면 관련 에러 응답
                log.info("[CustomOAuth2UserService] 다른 방식으로 가입된 이메일입니다. : {}", user.getAuthProvider());
                throw new OAuth2AuthenticationException("다른 방식으로 가입된 이메일입니다. : " + user.getAuthProvider());
            }
        } else {
            // DB에 해당 이메일의 일반 계정이 없으면, 새로운 유저 저장 후 반환
            User newUser = UserConverter.kakaoToUser(oAuth2Response.getEmail(), oAuth2Response.getNickName(), AuthProvider.KAKAO);
            userRepository.save(newUser);
            log.info("[CustomOAuth2UserService] 해당 Kakao 계정 최초 로그인입니다. DB에 유저 정보 생성 성공.");
            return new CustomOAuth2User(newUser);
        }
    }
}
