package com.timeplanner.dev.domain.auth.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationCodeRedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String VERIFY_PREFIX = "verify:";
    private static final String COOLDOWN_PREFIX = "cooldown:";
    private static final Duration CODE_TTL = Duration.ofMinutes(10); // 인증코드 유효시간
    private static final Duration COOLDOWN_TTL = Duration.ofMinutes(1); // 재요청 제한 시간

    private static final String ATTEMPT_PREFIX = "attempt:";
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration ATTEMPT_TTL = Duration.ofMinutes(10);
    private static final String VERIFIED_PREFIX = "verified:";

    public void save(String email, String code) {
        redisTemplate.opsForValue().set(VERIFY_PREFIX + email, code, CODE_TTL); // 인증코드 유효시간 10분
    }

    public boolean cooldownCheck(String email) {
        return redisTemplate.hasKey(COOLDOWN_PREFIX + email);
    }

    public boolean matches(String email, String code) {
        String stored = redisTemplate.opsForValue().get(VERIFY_PREFIX +email);

        return code.equals(stored);
    }

    public void remove(String email) {
        redisTemplate.delete(VERIFY_PREFIX + email);
    }

    public void markVerified(String email) {
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", Duration.ofMinutes(10));
    }

    public boolean isVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get(VERIFIED_PREFIX + email));
    }

    public boolean verifyCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(VERIFY_PREFIX +email);
        log.debug("redis stored code: {}", storedCode);

        if (storedCode == null) {
            throw new IllegalArgumentException("인증 코드가 만료되었거나 존재하지 않습니다.");
        }

        if (!storedCode.equalsIgnoreCase(inputCode)) {
            incrementAttempt(email);
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        markVerified(email);
        clearAttempts(email);

        return true;
    }

    private void incrementAttempt(String email) {
        String key = ATTEMPT_PREFIX + email;
        long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts == 1) {
            redisTemplate.expire(key, ATTEMPT_TTL);
        }
        if (attempts >= MAX_ATTEMPTS) {
            throw new IllegalStateException("인증 실패가 5회를 초과했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private void clearAttempts(String email) {
        redisTemplate.delete(ATTEMPT_PREFIX + email);
    }
}
