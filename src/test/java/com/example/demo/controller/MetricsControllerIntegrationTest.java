package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.BaseIntegrationTest;
import com.example.demo.model.CacheMetricsResponse;
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
        cacheMetricsService.incrementHitCount("users");
        cacheMetricsService.incrementMissCount("users");
        cacheMetricsService.incrementMissCount("users");
    }

    @Test
    @DisplayName("Should return cache metrics for an existing cache name")
    void testGetCacheMetrics_successful()
    {
        // GIVEN,WHEN
        final EntityExchangeResult<CacheMetricsResponse> result = webClient.get()
            .uri("/api/metrics/users")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody(CacheMetricsResponse.class)
            .returnResult();

        // THEN
        final CacheMetricsResponse metrics = result.getResponseBody();
        assertThat(metrics).isNotNull();
        assertThat(metrics.cacheName()).isEqualTo("users");
        assertThat(metrics.hits()).isEqualTo(1L);
        assertThat(metrics.misses()).isEqualTo(2L);
        assertThat(metrics.total()).isEqualTo(3L);
        assertThat(metrics.hitRate()).isEqualTo("33%");
    }
}
