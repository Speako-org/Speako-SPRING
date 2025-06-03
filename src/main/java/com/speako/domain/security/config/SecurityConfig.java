package com.speako.domain.security.config;

import com.speako.domain.security.service.CustomUserDetailsService;
import com.speako.domain.security.filter.JwtAuthenticationFilter;
import com.speako.domain.security.handler.CustomAuthenticationEntryPoint;
import com.speako.domain.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 정책 활성화 (커스텀한 corsConfigurationSource 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 설정 비활성화 (확장 가능성 고려하여 람다로 둠)
                .csrf(csrf -> csrf.disable())
                // 세션 미사용(STATELESS) 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 특정 URL 권한 설정
                .authorizeHttpRequests((requests) -> requests

                        // 인증 필요 대상에서 지정 url 제외
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/reissue"
                        ).permitAll()
                        // 지정 url 이외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 기본 로그인 폼 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 기본 HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 기본 로그아웃 비활성화
                .logout(LogoutConfigurer::disable)
                // 인증/인가 실패 시 커스텀 예외 처리 (미인증 사용자가 접근하는 경우도 포함)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
                // UsernamePasswordAuthenticationFilter 이전에 JwtAuthenticationFilter 넣기
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    //  CORS 정책 커스터마이징
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://localhost:3000",
                "https://speako.site"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CACHE_CONTROL,
                HttpHeaders.CONTENT_TYPE,
                "Refresh-Token"
        ));
        config.setAllowCredentials(Boolean.TRUE);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
