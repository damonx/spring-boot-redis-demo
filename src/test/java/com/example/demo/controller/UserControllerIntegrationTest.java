package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.BaseIntegrationTest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import java.time.Instant;
import java.util.List;
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
 * Integration tests for {@link UserController} using {@link WebTestClient}.
 */
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("User Controller Integration Tests")
@Testcontainers
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        // Ensure initial data is loaded
        userService.getAllUsers();
    }



    @Test
    @DisplayName("Should return a user by ID")
    void testGetUserById_successful() {
        // WHEN
        EntityExchangeResult<User> result = webClient.get()
            .uri("/api/users/1")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody(User.class)
            .returnResult();

        // THEN
        final User user = result.getResponseBody();
        assertThat(user).isNotNull();
        assertThat(user.name()).isEqualTo("Alice");
        assertThat(user.id()).isEqualTo(1L);
        assertThat(user.email()).isEqualTo("alice@example.com");
        assertThat(user.createdAt()).isEqualTo(Instant.parse("2025-10-06T00:00:00Z"));
    }

    @Test
    @DisplayName("Should bypass cache when 'bypassCache=true' is used")
    void testGetUserById_bypassCache() {
        // WHEN
        EntityExchangeResult<User> result = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/users/{id}")
                .queryParam("bypassCache", "true")
                .build(1L))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody(User.class)
            .returnResult();

        // THEN
        final User user = result.getResponseBody();
        assertThat(user).isNotNull();
        assertThat(user.name()).isEqualTo("Alice");
        assertThat(user.id()).isEqualTo(1L);
        assertThat(user.email()).isEqualTo("alice@example.com");
        assertThat(user.createdAt()).isEqualTo(Instant.parse("2025-10-06T00:00:00Z"));
    }
}