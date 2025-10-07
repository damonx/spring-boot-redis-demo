package com.example.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link CacheMetricsService} that stores cache metrics in Redis.
 */
@Service
public class CacheMetricsServiceImpl implements CacheMetricsService
{

    private static final String HIT_KEY_PREFIX = "metrics:cache:hit:";
    private static final String MISS_KEY_PREFIX = "metrics:cache:miss:";

    private final StringRedisTemplate redisTemplate;

    /**
     * Constructor with injection.
     *
     * @param redisTemplate the redis template.
     */
    public CacheMetricsServiceImpl(StringRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void incrementHitCount(String cacheName)
    {
        redisTemplate.opsForValue().increment(HIT_KEY_PREFIX + cacheName);
    }

    @Override
    public void incrementMissCount(String cacheName)
    {
        redisTemplate.opsForValue().increment(MISS_KEY_PREFIX + cacheName);
    }

    @Override
    public String getHitCount(String cacheName)
    {
        return getValue(HIT_KEY_PREFIX + cacheName);
    }

    @Override
    public String getMissCount(String cacheName)
    {
        return getValue(MISS_KEY_PREFIX + cacheName);
    }

    @Override
    public String getHitRate(final String cacheName)
    {
        long hits = Long.parseLong(getHitCount(cacheName));
        long misses = Long.parseLong(getMissCount(cacheName));
        long total = hits + misses;
        if (total > 0) {
            return Double.toString(total/(double)total);
        }
        return "0";
    }

    @Override
    public void resetMetrics(final String cacheName)
    {
        redisTemplate.delete(HIT_KEY_PREFIX + cacheName);
        redisTemplate.delete(MISS_KEY_PREFIX + cacheName);
    }

    private String getValue(final String key)
    {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
            .map(Long::parseLong)
            .map(String::valueOf)
            .orElse("0");
    }
}