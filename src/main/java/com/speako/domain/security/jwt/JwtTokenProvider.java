package com.speako.domain.security.jwt;

import com.speako.domain.auth.exception.SecurityErrorCode;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.global.apiPayload.exception.CustomException;
import com.speako.global.util.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RedisUtil redisUtil;

    public JwtTokenProvider(JwtProperties jwtProperties, RedisUtil redisUtil) {

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey()); // Secret Key 디코딩 (Base64)
        this.key = Keys.hmacShaKeyFor(keyBytes); // Key 객체 생성
        this.accessTokenExpiration = jwtProperties.accessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.refreshTokenExpiration();
        this.redisUtil = redisUtil;
    }

    // 공통 토큰 provider
    private String createToken(CustomUserDetails customUserDetails, long tokenExpiration, String type) {
        return Jwts.builder()
                .setSubject(customUserDetails.getEmail()) // 사용자 고유 ID
                .claim("id", customUserDetails.getId()) // 추가 클레임
                .claim("type", type)
                .setIssuedAt(Date.from(Instant.now())) // 토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plusSeconds(tokenExpiration))) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 키 및 알고리즘
                .compact();
    }

    // access 토큰 생성
    public String createJwtAccessToken(CustomUserDetails customUserDetails) {

        log.info("[JwtTokenProvider] access 토큰 생성을 시작합니다.");
        return createToken(customUserDetails, accessTokenExpiration, "accessToken");
    }

    // refresh 토큰 생성
    public String createJwtRefreshToken(CustomUserDetails customUserDetails) {

        log.info("[JwtTokenProvider] refresh 토큰 생성을 시작합니다.");
        // 기존 refresh 토큰 존재하는 경우 삭제
        redisUtil.deleteRefresh(customUserDetails.getEmail());
        String refreshToken = createToken(customUserDetails, refreshTokenExpiration, "refreshToken");
        redisUtil.saveRefresh(customUserDetails.getEmail(), refreshToken, refreshTokenExpiration);
        return refreshToken;
    }

    // JWT 토큰에서 사용자 email 추출 (유효성 검증된 토큰이어야만 함)
    public String getEmail(String token) {

        log.info("[JwtTokenProvider] 토큰에서 email을 추출합니다.");
        return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }

    // JWT 토큰의 유효성 검증
    public boolean validateToken(String token) {

        log.info("[JwtTokenProvider] JWT 토큰 유효성 검사를 시작합니다.");
        try {
            // 블랙리스트 액세스 토큰인지 (로그아웃된 토큰인지) 판단
            if ("blacklisted".equals(redisUtil.get(token))) {
                log.warn("[JwtTokenProvider] 해당 토큰은 블랙리스트 토큰입니다. (로그아웃된 토큰)");
                throw new CustomException(SecurityErrorCode.INVALID_ACCESS_TOKEN);
            }
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // 시그니처 검증 실패, 포맷 오류, 지원하지 않는 형식 등
            throw new SecurityException("잘못된 토큰 형식입니다.");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰입니다.");
        } catch (CustomException e) {
            throw new InsufficientAuthenticationException("블랙리스트 처리된 토큰입니다.");
        }
    }

    // HttpServletRequest 에서 Token 추출
    public String resolveRequestToToken(HttpServletRequest request) {

        log.info("[JwtTokenProvider] HttpServletRequest에서부터 토큰을 추출합니다.");
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    // 만료까지 남은 시간 확인
    public long getExpiration(String token) {

        log.info("[JwtTokenProvider] 토큰의 만료까지 남은 시간을 확인합니다.");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            long expiration = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000; // 초 단위로 변환
            log.info("[JwtTokenProvider] 토큰의 만료까지 남은 시간 : {}", expiration);
            return expiration;
            
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(SecurityErrorCode.INVALID_ACCESS_TOKEN);
        }
    }
}
