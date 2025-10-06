package com.example.demo.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

/**
 * A tracker to find hot users.
 */
@Component
public class UserAccessTracker
{
    private static final String HOT_USERS_KEY = "hotUsers";

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    public void recordAccess(Long userId)
    {
        redisTemplate.opsForZSet().incrementScore(HOT_USERS_KEY, userId, 1);
        // Optional: set expiry so stale users are removed automatically
        redisTemplate.expire(HOT_USERS_KEY, 1, java.util.concurrent.TimeUnit.HOURS);
    }

    public Set<Long> getTopHotUsers(int topN)
    {
        // Get top N users by score descending
        return redisTemplate.opsForZSet()
            .reverseRange(HOT_USERS_KEY, 0, topN - 1);
    }
}
