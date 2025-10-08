package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that require a Redis container.
 *
 * <p>This class starts a Redis Testcontainer once per test suite
 * and registers its host/port for Spring Boot auto-configuration.
 *
 * <p>All subclasses automatically get a running Redis instance.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
public class BaseIntegrationTest {

    /** Shared Redis Testcontainer instance for all integration tests. */
    @Container
    protected static final GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    /**
     * Registers Redis connection properties so that Spring Data Redis
     * automatically connects to the container instance.
     */
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @AfterAll
    static void afterAll()
    {
        redis.stop();
    }
}
