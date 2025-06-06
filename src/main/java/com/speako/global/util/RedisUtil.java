package com.speako.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_PREFIX = "refresh:";

    // refresh 토큰 저장
    public void saveRefresh(String email, String refreshToken, long refreshTokenExpiration) {

        log.info("[RedisUtil] Redis에 refresh 토큰을 저장합니다.");
        redisTemplate.opsForValue().set(REFRESH_PREFIX + email, refreshToken, Duration.ofSeconds(refreshTokenExpiration));
    }

    // 기존 refresh 토큰 존재하는 경우 삭제 (refresh 토큰 발급 전에 실행 됨)
    public void deleteRefresh(String email) {

        log.info("[RedisUtil] 특정 email에 해당하는 refresh 토큰이 있을 경우 삭제합니다.");
        redisTemplate.delete(REFRESH_PREFIX + email);
    }

    // 주어진 키에 대해, TTL을 포함하여 Redis에 저장
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 해당 key에 대응하는 문자열 값 반환
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // key 값을 1 증가시키고, TTL 없으면 설정
    public void increment(String key, long timeoutSeconds) {
        redisTemplate.opsForValue().increment(key);
        expireIfNotSet(key, timeoutSeconds);
    }

    // key에 해당하는 데이터 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // key에 TTL 설정
    public void expire(String key, long timeoutSeconds) {
        redisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds));
    }

    // TTL 설정되어 있지 않은 경우, 새로 TTL 설정
    private void expireIfNotSet(String key, long timeoutSeconds) {

        Long expire = redisTemplate.getExpire(key);
        /*
          TTL 값이
          - -1(설정되어 있지 않음(영구저장))
          - 0(남아 있지 않으며 곧 삭제될 예정)
          - null(키가 없거나 조회 실패)
          인 경우에 TTL 설정해주기
         */
        if (expire == null || expire == -1L || expire == 0L) {
            redisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds));
        }
    }
}
