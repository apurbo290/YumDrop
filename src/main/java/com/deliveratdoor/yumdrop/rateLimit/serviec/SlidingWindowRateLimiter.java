package com.deliveratdoor.yumdrop.rateLimit.serviec;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
public class SlidingWindowRateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final int LIMIT = 100;        // requests
    private static final int WINDOW_SEC = 60;    // window size

    public SlidingWindowRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String userId) {
        String key = "rate_limit:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_SEC * 1000L;

        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

        // Remove old requests
        zSet.removeRangeByScore(key, 0, windowStart);

        // Count current requests
        Long count = zSet.zCard(key);

        if (Objects.nonNull(count) && count >= LIMIT) {
            return false;
        }

        // Add new request
        zSet.add(key, String.valueOf(now), now);

        // Auto cleanup
        redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SEC));

        return true;
    }
}

