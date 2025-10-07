package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.BaseIntegrationTest;
import com.example.demo.service.CacheMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

/**
 * Integration tests for {@link MetricsController} using {@link WebTestClient}.
 */
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Metrics Controller Integration Tests")
@Testcontainers
class MetricsControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private CacheMetricsService cacheMetricsService;

    @BeforeEach
    void setup()
    {
        cacheMetricsService.resetMetrics("users");
        // Preload some metrics data before tests
        cacheMetricsService.incrementHitCount("users");
        cacheMetricsService.incrementMissCount("users");
        cacheMetricsService.incrementMissCount("users");
    }

    @Test
    @DisplayName("Should return cache metrics for an existing cache name")
    void testGetCacheMetrics_successful()
    {
        // WHEN
        final EntityExchangeResult<Map<String, String>> result = webClient.get()
            .uri("/api/metrics/users")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody(new org.springframework.core.ParameterizedTypeReference<Map<String, String>>()
            {
            })
            .returnResult();

        // THEN
        final Map<String, String> metrics = result.getResponseBody();
        assertThat(metrics).isNotNull();
        assertThat(metrics).containsKeys("hits", "misses");
        assertThat(Long.parseLong(metrics.get("hits"))).isGreaterThanOrEqualTo(1);
        assertThat(Long.parseLong(metrics.get("misses"))).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should return zero metrics when cache name not found")
    void testGetCacheMetrics_notFound()
    {
        // WHEN
        final EntityExchangeResult<Map<String, String>> result = webClient.get()
            .uri("/api/metrics/nonexistent")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody(new org.springframework.core.ParameterizedTypeReference<Map<String, String>>()
            {
            })
            .returnResult();

        // THEN
        final Map<String, String> metrics = result.getResponseBody();
        assertThat(metrics).isNotNull();
        assertThat(Long.parseLong(metrics.get("hits"))).isZero();
        assertThat(Long.parseLong(metrics.get("misses"))).isZero();
    }
}
