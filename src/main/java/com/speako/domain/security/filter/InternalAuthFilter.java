package com.speako.domain.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speako.domain.auth.exception.SecurityErrorCode;
import com.speako.global.apiPayload.CustomResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
// 내부에서 호출되는 API 요청에 대하여 Shared Secret 검증하는 필터
public class InternalAuthFilter extends OncePerRequestFilter {

    // FastAPI & Spring 사이에서 사용되는 internal shared-secret 값
    private final String internalSecret;
    
    public InternalAuthFilter(@Value("${fastapi.internal-secret}") String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 내부 API 요청만 검증
        String uri = request.getRequestURI();
        if ((uri.startsWith("/api/transcriptions/") && uri.endsWith("/complete"))
                || uri.equals("/api/analyze/complete")) {

            log.info("[InternalAuthFilter] 내부 API 요청을 검증합니다.");

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.equals("Bearer " + internalSecret)) {

                log.warn("[InternalAuthFilter] AUTH401-7, 내부 API 인증에 실패했습니다. (Shared Secret 불일치)");

                ObjectMapper objectMapper = new ObjectMapper();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                        CustomResponse.onFailure(SecurityErrorCode.INTERNAL_AUTH_SECRET_INVALID)));
                return;
            }
            log.info("[InternalAuthFilter] 내부 API 요청 검증 성공");
        }
        filterChain.doFilter(request, response);
    }
}
