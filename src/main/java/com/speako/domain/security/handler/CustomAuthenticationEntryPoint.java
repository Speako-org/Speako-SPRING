package com.speako.domain.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speako.global.apiPayload.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 커스텀 에러응답 형식의 json 문자열 생성
        String jsonErrorResponse = objectMapper.writeValueAsString(CustomResponse.onFailure("AUTH401-1", "인증이 필요합니다."));
        response.getWriter().write(jsonErrorResponse);
    }
}
