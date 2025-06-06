package com.speako.domain.auth.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speako.domain.auth.oauth.userinfo.CustomOAuth2User;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.security.jwt.JwtTokenProvider;
import com.speako.global.apiPayload.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    // 카카오 로그인 성공 시 실행
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        // CustomOAuth2User의 user 기반으로 CustomUserDetails 생성
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        CustomUserDetails customUserDetails = new CustomUserDetails(customOAuth2User.getUser());

        // JWT 발급
        log.info("[OAuth2SuccessHandler] 로그인 계정에 대한 access/refresh 토큰을 발급합니다.");
        String accessToken = jwtTokenProvider.createJwtAccessToken(customUserDetails);
        String refreshToken = jwtTokenProvider.createJwtRefreshToken(customUserDetails);
        log.info("[OAuth2SuccessHandler] access/refresh 토큰 발급 성공");

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        // 커스텀 성공 응답 형식의 json 문자열 생성
        String jsonSuccessResponse = objectMapper.writeValueAsString(CustomResponse.onSuccess(result));
        response.getWriter().write(jsonSuccessResponse);
    }
}
