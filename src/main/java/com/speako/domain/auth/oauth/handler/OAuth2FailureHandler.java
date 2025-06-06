package com.speako.domain.auth.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speako.global.apiPayload.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        // 기본 에러 메세지 설정
        String errorMessage = "카카오 로그인 중 오류가 발생했습니다.";

        // CustomOAuth2UserService에서 발생한 OAuth2AuthenticationException 에러일 경우, 에러메세지 받아오기
        if (exception instanceof OAuth2AuthenticationException) {
            errorMessage = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 커스텀 에러응답 형식의 json 문자열 생성
        String jsonErrorResponse = objectMapper.writeValueAsString(CustomResponse.onFailure("OAUTH401-1", errorMessage));
        response.getWriter().write(jsonErrorResponse);
    }
}
