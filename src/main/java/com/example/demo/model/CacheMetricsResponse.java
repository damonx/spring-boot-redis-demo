package com.example.demo.model;

/**
 * Immutable record representing cache metrics.
 *
 * @param cacheName The name of the cache being measured.
 * @param hits      The total number of cache hits.
 * @param misses    The total number of cache misses.
 * @param hitRate   The hit rate of the cache (as a percentage string or ratio).
 */
public record CacheMetricsResponse(
    String cacheName,
    long hits,
    long misses,
    long total,
    String hitRate
) {}
