package com.example.demo.service;

import com.example.demo.model.CacheMetricsResponse;
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
    public CacheMetricsResponse generateCacheMetrics(final String cacheName)
    {
        final long hits = getHitCount(cacheName);
        final long misses = getMissCount(cacheName);
        return new CacheMetricsResponse(cacheName, hits, misses, hits + misses, getHitRate(cacheName));
    }

    @Override
    public void incrementMissCount(String cacheName)
    {
        redisTemplate.opsForValue().increment(MISS_KEY_PREFIX + cacheName);
    }

    @Override
    public void resetMetrics(final String cacheName)
    {
        redisTemplate.delete(HIT_KEY_PREFIX + cacheName);
        redisTemplate.delete(MISS_KEY_PREFIX + cacheName);
    }

    private long getHitCount(String cacheName)
    {
        return getValue(HIT_KEY_PREFIX + cacheName);
    }

    private long getMissCount(String cacheName)
    {
        return getValue(MISS_KEY_PREFIX + cacheName);
    }

    private String getHitRate(final String cacheName)
    {
        long hits = getHitCount(cacheName);
        long misses = getMissCount(cacheName);
        long total = hits + misses;
        if (total == 0) {
            return "0%";
        }
        double rate = (double) hits / total * 100;
        return String.format("%.0f%%", rate);
    }

    private long getValue(final String key)
    {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
            .map(Long::parseLong)
            .orElse(0L);
    }
}