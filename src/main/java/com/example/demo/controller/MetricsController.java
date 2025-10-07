package com.example.demo.controller;

import com.example.demo.model.CacheMetricsResponse;
import com.example.demo.service.CacheMetricsService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing cache metrics via HTTP endpoints.
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final CacheMetricsService metricsService;

    public MetricsController(CacheMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Retrieves cache metrics (hits, misses, hit rate) for a given cache.
     *
     * @param cacheName the name of the cache
     * @return a map of metrics
     */
    /**
     * Retrieves metrics for a specific cache.
     *
     * @param cacheName The cache name.
     * @return A {@link CacheMetricsResponse} containing hit/miss counts and hit rate.
     */
    @GetMapping("/{cacheName}")
    public CacheMetricsResponse getMetrics(@PathVariable String cacheName) {
        return metricsService.generateCacheMetrics(cacheName);
    }

    /**
     * Resets the metrics for a given cache.
     *
     * @param cacheName the cache name
     */
    @DeleteMapping("/{cacheName}")
    public void resetMetrics(@PathVariable String cacheName) {
        metricsService.resetMetrics(cacheName);
    }
}
