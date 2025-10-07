package com.example.demo.controller;

import com.example.demo.service.CacheMetricsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("/{cacheName}")
    public Map<String, String> getMetrics(@PathVariable String cacheName) {
        return Map.of(
            "cacheName", cacheName,
            "hits", metricsService.getHitCount(cacheName),
            "misses", metricsService.getMissCount(cacheName),
            "hitRate", metricsService.getHitRate(cacheName)
        );
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
