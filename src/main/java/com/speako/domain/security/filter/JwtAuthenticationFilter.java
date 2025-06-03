package com.speako.domain.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speako.domain.security.service.CustomUserDetailsService;
import com.speako.domain.security.jwt.JwtTokenProvider;
import com.speako.global.apiPayload.CustomResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // HttpServletRequest 에서 Token 추출
        String token = jwtTokenProvider.resolveRequestToToken(request);
        // 토큰이 존재하고 유효한지 판단
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰에서 사용자 정보 추출
                String email = jwtTokenProvider.getEmail(token);
                // 이메일 기반 유저 정보 조회
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // SecurityContext 에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SecurityException | ExpiredJwtException e) {
            setCustomErrorResponse(response, "AUTH401-3", "유효하지 않은 Access 토큰입니다.");
            return;
        } catch (InsufficientAuthenticationException e) {
            setCustomErrorResponse(response, "AUTH401-4", "블랙리스트 처리된 토큰입니다.");
            return;
        } catch (Exception e) {
            setCustomErrorResponse(response, "AUTH401-5", "JWT 인증 중 오류가 발생했습니다.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 커스텀 에러응답 형식의 json 문자열 생성
    private void setCustomErrorResponse(HttpServletResponse response, String code, String message) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String jsonErrorResponse = objectMapper.writeValueAsString(CustomResponse.onFailure(code, message));

        response.getWriter().write(jsonErrorResponse);
    }
}
