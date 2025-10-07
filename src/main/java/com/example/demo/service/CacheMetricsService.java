package com.example.demo.service;

import com.example.demo.model.CacheMetricsResponse;

/**
 * Service interface for managing cache metrics such as hit and miss counts.
 * <p>
 * Implementations of this interface are responsible for tracking and
 * retrieving statistics for different cache regions (e.g., "users", "products").
 * </p>
 */
public interface CacheMetricsService {

    /**
     * Increments the hit count for a given cache name.
     *
     * @param cacheName the name of the cache
     */
    void incrementHitCount(String cacheName);

    /**
     * Increments the miss count for a given cache name.
     *
     * @param cacheName the name of the cache
     */
    void incrementMissCount(String cacheName);

    /**
     * Clears all metrics for a given cache.
     *
     * @param cacheName the cache name
     */
    void resetMetrics(String cacheName);

    /**
     * Generates cache metrics for a given cache.
     * @param cacheName the cache name
     * @return the instance of {@link CacheMetricsResponse}
     */
    CacheMetricsResponse generateCacheMetrics(String cacheName);
}