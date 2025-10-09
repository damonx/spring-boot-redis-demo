package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

/**
 * Base configuration class for integration tests.  This currently contains
 * customisation for setting the response timeout on WebTestClient.
 */
@TestConfiguration
public class TestConfigBase
{
    private static final int TEST_TIMEOUT = 10;

    /**
     * @return a customiser for WebTestClient.
     */
    @Bean
    public WebTestClientBuilderCustomizer webClientCustomiser()
    {
        return builder -> builder.responseTimeout(Duration.ofSeconds(TEST_TIMEOUT));
    }
}
