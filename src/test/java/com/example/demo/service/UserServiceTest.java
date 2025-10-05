package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.demo.model.User;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Testcontainers
@SpringBootTest
@Tag("IntegrationTest")
@DisplayName("User Service Redis Test...")
class UserServiceTest {

    @Container
    static RedisContainer redis = new RedisContainer("redis:7.0.11-alpine");

    @DynamicPropertySource
    static void redisProperties(final DynamicPropertyRegistry registry)
    {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private UserService userService;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setup() {
        // clear cache before each test
        cacheManager.getCache("users").clear();
    }

    @Test
    @DisplayName("Test measuring the first normal fetch user from database and second fetch user from cache.")
    void testCacheable() {
        final Duration t1 = measure(() -> userService.getUserById(1L));
        final Duration t2 = measure(() -> userService.getUserById(1L));

        assertThat(t2).isLessThan(t1.dividedBy(2));
    }

    @Test
    @DisplayName("Test fetching from cache got the same user having same value.")
    void testCacheFeachValues() {
        final User userFromDb = userService.getUserById(1L);
        assertThat(userFromDb).isNotNull();
        assertThat(userFromDb.name()).isEqualTo("Alice");
        assertThat(userFromDb.email()).isEqualTo("alice@example.com");
        final User userFromCache = userService.getUserById(1L);
        assertThat(userFromCache).isNotNull();
        assertThat(userFromCache).usingRecursiveComparison().isEqualTo(userFromDb);
    }

    @Test
    @DisplayName("Test addUser stores user and caches it")
    void testAddUser() {
        User newUser = new User(3L, "Charlie", "charlie@example.com", Instant.now());
        User added = userService.addUser(newUser);

        assertThat(added).isEqualTo(newUser);

        // Fetch from cache
        User cached = cacheManager.getCache("users").get(3L, User.class);
        assertThat(cached).isEqualTo(newUser);

        // Fetch from service -> should be cached
        User fetched = userService.getUserById(3L);
        assertThat(fetched).isEqualTo(newUser);
    }

    @Test
    @DisplayName("Test removeUser evicts cache")
    void testRemoveUser() {
        User newUser = new User(4L, "Diana", "diana@example.com", Instant.now());
        userService.addUser(newUser);

        User cachedBefore = cacheManager.getCache("users").get(4L, User.class);
        assertThat(cachedBefore).isEqualTo(newUser);

        userService.removeUser(4L);

        assertThat(cacheManager.getCache("users").get(4L)).isNull();

        assertThat(userService.getUserById(4L)).isNull();
    }

    @Test
    @DisplayName("Test updateUser updates cache")
    void testUpdateUser() {
        User original = new User(5L, "Eve", "eve@example.com", Instant.now());
        userService.addUser(original);

        // Update user
        User updated = new User(5L, "Eve", "eve_new@example.com", Instant.now());
        userService.updateUser(updated);

        // Cache should reflect updated user
        User cached = cacheManager.getCache("users").get(5L, User.class);
        assertThat(cached.email()).isEqualTo("eve_new@example.com");

        // Fetch from service -> should return updated user
        User fetched = userService.getUserById(5L);
        assertThat(fetched.email()).isEqualTo("eve_new@example.com");
    }

    @Test
    @DisplayName("Test getAllUsers returns immutable map")
    void testGetAllUsers() {
        Map<Long, User> users = userService.getAllUsers();
        assertThat(users).isNotEmpty();

        // Map should be unmodifiable
        assertThatThrownBy(() -> users.put(99L, new User(99L, "X", "x@example.com", Instant.now())))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    private Duration measure(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return Duration.ofNanos(System.nanoTime() - start);
    }
}