package com.example.demo.service;

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
     * Retrieves the number of hits for a given cache.
     *
     * @param cacheName the cache name
     * @return the number of cache hits
     */
    String getHitCount(String cacheName);

    /**
     * Retrieves the number of misses for a given cache.
     *
     * @param cacheName the cache name
     * @return the number of cache misses
     */
    String getMissCount(String cacheName);

    /**
     * Calculates the cache hit rate as a ratio of hits to total requests.
     *
     * @param cacheName the cache name
     * @return the cache hit rate, between 0.0 and 1.0
     */
    String getHitRate(String cacheName);

    /**
     * Clears all metrics for a given cache.
     *
     * @param cacheName the cache name
     */
    void resetMetrics(String cacheName);
}