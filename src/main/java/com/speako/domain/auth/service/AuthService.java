package com.speako.domain.auth.service;

import com.speako.domain.auth.dto.reqDTO.LoginRequest;
import com.speako.domain.auth.dto.reqDTO.SignupRequest;
import com.speako.domain.auth.dto.resDTO.JwtResponse;
import com.speako.domain.auth.dto.resDTO.LoginResponse;
import com.speako.domain.auth.exception.SecurityErrorCode;
import com.speako.domain.challenge.service.command.UserChallengeService;
import com.speako.domain.challenge.service.query.BadgeQueryService;
import com.speako.domain.security.jwt.JwtTokenProvider;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.user.converter.UserConverter;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.exception.UserErrorCode;
import com.speako.domain.user.repository.UserRepository;
import com.speako.domain.userinfo.converter.UserAchievementConverter;
import com.speako.domain.userinfo.domain.UserAchievement;
import com.speako.domain.userinfo.repository.UserAchievementRepository;
import com.speako.global.apiPayload.exception.CustomException;
import com.speako.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BadgeQueryService badgeQueryService;
    private final UserAchievementRepository userAchievementRepository;
    private final UserChallengeService userChallengeService;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    @Transactional
    public Long signup(SignupRequest signupRequest) {

        log.info("[AuthService] 회원가입 로직을 시작합니다.");
        if (userRepository.findByEmail(signupRequest.email()).isPresent()) {
            log.warn("[AuthService] 입력된 이메일에 해당하는 유저계정이 이미 존재합니다.");
            throw new CustomException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
        User user = userRepository.save(UserConverter.toUser(signupRequest, bCryptPasswordEncoder));

        // UserAchievement Initialize
        int totalBadgeCount = badgeQueryService.countTotalBadges(); // 전체 뱃지 종류 수 카운트
        UserAchievement userAchievement = UserAchievementConverter.toUserAchievement(user, totalBadgeCount);
        userAchievementRepository.save(userAchievement);

        // Challenge Initialize
        userChallengeService.initializeUserChallenges(user);

        log.info("[AuthService] 회원가입 완료");
        return user.getId();
    }

    // 로그인
    public LoginResponse login(LoginRequest loginRequest) {

        log.info("[AuthService] 로그인 로직을 시작합니다.");
        int maxFail = 5;
        final long duration = 10 * 60; // 10분

        // 키값을 사용하여 Redis에 저장된 실패 횟수 String 가져오기
        String redisKey = "login:fail:" + loginRequest.email();
        String failCountString = redisUtil.get(redisKey);

        // 조회 결과에 따른 failCount(실패 횟수) 초기화
        int failCount = (failCountString == null) ? 0 : Integer.parseInt(failCountString);

        // 최대 로그인 시도 가능 횟수 초과 시 에러
        if (failCount >= maxFail) {
            log.warn("[AuthService] 최대 로그인 시도 가능 횟수를 초과하였습니다.");
            throw new CustomException(SecurityErrorCode.TOO_MANY_LOGIN_ATTEMPTS);
        }
        /*
          이메일과 비번을 통한 사용자 검증
          실패 시:
          1. 로그인 실패 횟수 증가
          2. TTL 갱신
          3. 에러 반환 (이메일/비번 중 어느것을 틀렸는지 유추하는 것을 막기 위해 에러 통일
        */
        Optional<User> user = userRepository.findByEmail(loginRequest.email());
        // 이메일 검증
        if (user.isEmpty()) {
            log.warn("[AuthService] 이메일 {} 에 대해 로그인 실패하였습니다.", loginRequest.email());
            redisUtil.increment(redisKey, duration);
            redisUtil.expire(redisKey, duration);
            throw new CustomException(SecurityErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }
        // 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(loginRequest.password(), user.get().getPassword())) {
            log.warn("[AuthService] 해당 비밀번호를 통한 로그인에 실패하였습니다.");
            redisUtil.increment(redisKey, duration);
            redisUtil.expire(redisKey, duration);
            throw new CustomException(SecurityErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }
        // 로그인 성공 시 실패 기록 삭제
        log.warn("[AuthService] 입력된 이메일&비밀번호가 기존에 회원가입한 계정 정보와 일치합니다.");
        redisUtil.delete(redisKey);

        // CustomUserDetails 객체 생성 및 Access/Refresh 토큰 발급
        CustomUserDetails customUserDetails = CustomUserDetails.toCustomUserDetails(user.get());
        String accessToken = jwtTokenProvider.createJwtAccessToken(customUserDetails);
        String refreshToken = jwtTokenProvider.createJwtRefreshToken(customUserDetails);

        log.warn("[AuthService] 로그인 성공");
        return new LoginResponse(user.get().getId(), accessToken, refreshToken);
    }

    // 로그아웃
    public void logout(HttpServletRequest httpServletRequest) {

        log.warn("[AuthService] 로그아웃 로직을 시작합니다.");
        // HttpServletRequest에서 access 토큰 가져온 후 null 체크
        String accessToken = jwtTokenProvider.resolveRequestToToken(httpServletRequest);
        if (accessToken == null) {
            log.warn("[AuthService] HttpServletRequest에 access 토큰이 존재하지 않습니다.");
            throw new CustomException(SecurityErrorCode.INVALID_ACCESS_TOKEN);
        }
        // access 토큰 유효성 검사 후 블랙리스트 처리
        try {
            if (jwtTokenProvider.validateToken(accessToken)) {
                long expirationSeconds = jwtTokenProvider.getExpiration(accessToken);
                if (expirationSeconds > 0) {
                    log.info("[AuthService] access 토큰을 Redis의 blacklist 목록에 추가합니다.");
                    redisUtil.set(accessToken, "blacklisted", expirationSeconds, TimeUnit.SECONDS);
                }
            }
        } catch (SecurityException | ExpiredJwtException e) {
            throw new CustomException(SecurityErrorCode.INVALID_ACCESS_TOKEN);
        } catch (InsufficientAuthenticationException e) {
            throw new CustomException(SecurityErrorCode.BLACKLISTED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new CustomException(SecurityErrorCode.JWT_AUTHENTICATION_ERROR);
        }
        // refresh 토큰 삭제
        redisUtil.deleteRefresh(jwtTokenProvider.getEmail(accessToken));
    }

    // 토큰 재발급
    public JwtResponse reissue(String refreshToken) {

        log.warn("[AuthService] 토큰 재발급 로직을 시작합니다.");
        // refresh 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(SecurityErrorCode.INVALID_REFRESH_TOKEN);
        }
        String email = jwtTokenProvider.getEmail(refreshToken);
        // Redis에 저장된 토큰과 비교
        String storedToken = redisUtil.get("refresh:" + email);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new CustomException(SecurityErrorCode.INVALID_REFRESH_TOKEN);
        }
        // CustomUserDetails 객체 생성해서 access/refresh 토큰 발급
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND_BY_EMAIL));
        CustomUserDetails userDetails = CustomUserDetails.toCustomUserDetails(user);
        String newAccess = jwtTokenProvider.createJwtAccessToken(userDetails);
        String newRefresh = jwtTokenProvider.createJwtRefreshToken(userDetails);

        return new JwtResponse(newAccess, newRefresh);
    }
}
