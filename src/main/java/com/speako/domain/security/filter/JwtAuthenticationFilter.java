package com.speako.domain.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speako.domain.auth.exception.SecurityErrorCode;
import com.speako.domain.security.jwt.JwtTokenProvider;
import com.speako.domain.security.principal.CustomUserDetailsService;
import com.speako.global.apiPayload.CustomResponse;
import com.speako.global.apiPayload.code.BaseErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("[JwtAuthenticationFilter] JWT 인가 필터를 시작합니다.");

        // 소셜 로그인 인증 흐름 중인 요청은 무시
        String uri = request.getRequestURI();
        if (uri.startsWith("/login/oauth2/") || uri.startsWith("/oauth2/")) {
            log.info("[JwtAuthenticationFilter] 소셜 로그인 관련 요청을 넘깁니다.");
            filterChain.doFilter(request, response);
            return;
        }
        // 내부 API 요청은 무시
        if ((uri.startsWith("/api/transcriptions/") && uri.endsWith("/complete"))
                || uri.equals("/api/analyze/complete")) {
            log.info("[JwtAuthenticationFilter] 내부 API 요청의 JWT 검증을 건너뜁니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // HttpServletRequest 에서 Token 추출
        String token = jwtTokenProvider.resolveRequestToToken(request);
        // 토큰이 존재하고 유효한지 판단
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰에서 사용자 정보 추출
                String email = jwtTokenProvider.getEmail(token);
                // 이메일 기반 유저 정보 조회
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                log.info("[JwtAuthenticationFilter] 인증 객체를 생성 및 SecurityContext에 저장합니다.");
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // SecurityContext 에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SecurityException | ExpiredJwtException e) {
            log.warn("[JwtAuthenticationFilter] AUTH401-3, 유효하지 않은 Access 토큰입니다.");
            setCustomErrorResponse(response, SecurityErrorCode.INVALID_ACCESS_TOKEN);
            return;
        } catch (InsufficientAuthenticationException e) {
            log.warn("[JwtAuthenticationFilter] AUTH401-4, 블랙리스트 처리된 토큰입니다.");
            setCustomErrorResponse(response, SecurityErrorCode.BLACKLISTED_ACCESS_TOKEN);
            return;
        } catch (Exception e) {
            log.warn("[JwtAuthenticationFilter] AUTH401-5, JWT 인증 중 오류가 발생했습니다.");
            setCustomErrorResponse(response, SecurityErrorCode.JWT_AUTHENTICATION_ERROR);
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 커스텀 에러응답 형식의 json 문자열 생성
    private void setCustomErrorResponse(HttpServletResponse response, BaseErrorCode errorCode) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String jsonErrorResponse = objectMapper.writeValueAsString(CustomResponse.onFailure(errorCode));

        response.getWriter().write(jsonErrorResponse);
    }
}
