package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.demo.model.User;
import com.example.demo.scheduler.RefreshAheadScheduler;
import com.example.demo.tracker.UserAccessTracker;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("User Service Redis Test with Redis running in TestContainer.")
class UserServiceTest {
    @MockBean
    private RefreshAheadScheduler refreshAheadScheduler;

    @MockBean
    private UserAccessTracker userAccessTracker;

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
        // GIVEN,WHEN
        final Duration t1 = measure(() -> userService.getUserById(1L));
        final Duration t2 = measure(() -> userService.getUserById(1L));
        // THEN
        assertThat(t2).isLessThan(t1.dividedBy(2));
    }

    @Test
    @DisplayName("Test measuring the getUserByIdBypassingCache is slower than get.")
    void testCacheableBypassingCache() {
        // GIVEN,WHEN
        // Warm up the cache.
        userService.getUserById(1L);
        // Fetch user from cache.
        final Duration t2 = measure(() -> userService.getUserById(1L));
        // Bypassing cache
        final Duration t3 = measure(() -> userService.getUserByIdBypassCache(1L));

        // THEN
        assertThat(t2).isLessThan(t3.dividedBy(2));
    }

    @Test
    @DisplayName("Test fetching from cache got the same user having same value.")
    void testCacheFeachValues() {
        // GIVEN,WHEN
        final User userFromDb = userService.getUserById(1L);
        final User userFromCache = userService.getUserById(1L);
        // THEN
        assertThat(userFromDb).isNotNull();
        assertThat(userFromDb.name()).isEqualTo("Alice");
        assertThat(userFromDb.email()).isEqualTo("alice@example.com");
        assertThat(userFromDb.createdAt()).isEqualTo(Instant.parse("2025-10-06T00:00:00Z"));
        assertThat(userFromCache).isNotNull();
        assertThat(userFromCache).usingRecursiveComparison().isEqualTo(userFromDb);
    }

    @Test
    @DisplayName("Test addUser stores user and caches it")
    void testAddUser() {
        // GIVEN
        final User newUser = new User(3L, "Charlie", "charlie@example.com", Instant.now());
        // WHEN
        final User added = userService.addUser(newUser);
        // THEN
        final User cached = cacheManager.getCache("users").get(3L, User.class);
        final User fetched = userService.getUserById(3L);
        assertThat(added).isEqualTo(newUser);
        assertThat(cached).isEqualTo(newUser);

        // Fetch from service -> should be cached
        assertThat(fetched).isEqualTo(newUser);
    }

//    @Test
//    @DisplayName("Test removeUser evicts cache")
//    void testRemoveUser() {
//        final User newUser = new User(4L, "Diana", "diana@example.com", Instant.now());
//        userService.addUser(newUser);
//
//        final User cachedBefore = cacheManager.getCache("users").get(4L, User.class);
//        assertThat(cachedBefore).isEqualTo(newUser);
//
//        userService.removeUser(4L);
//
//        assertThat(cacheManager.getCache("users").get(4L)).isNull();
//        assertThat(userService.getUserById(4L)).isNull();
//    }

    @Test
    @DisplayName("Test updateUser updates cache")
    void testUpdateUser() {
        final User original = new User(5L, "Eve", "eve@example.com", Instant.now());
        userService.addUser(original);

        // Update user
        final User updated = new User(5L, "Eve", "eve_new@example.com", Instant.now());
        userService.updateUser(updated);

        // Cache should reflect updated user
        final User cached = cacheManager.getCache("users").get(5L, User.class);
        assertThat(cached.email()).isEqualTo("eve_new@example.com");

        // Fetch from service -> should return updated user
        final User fetched = userService.getUserById(5L);
        assertThat(fetched.email()).isEqualTo("eve_new@example.com");
    }

    @Test
    @DisplayName("Test getAllUsers returns immutable map.")
    void testGetAllUsers() {
        // GIVEN
        userService.removeAllUsers();
        userService.addUser(new User(1L, "Alice", "alice@example.com", Instant.parse("2025-10-06T00:00:00Z")));
        userService.addUser(new User(2L, "Bob", "bob@example.com", Instant.parse("2025-10-06T01:00:00Z")));

        // WHEN
        final Map<Long, User> users = userService.getAllUsers();
        // THEN
        assertThat(users)
            .hasSize(2)
            .anySatisfy((key, user) -> {
                assertThat(key).isEqualTo(1L);
                assertThat(user.name()).isEqualTo("Alice");
                assertThat(user.email()).isEqualTo("alice@example.com");
                assertThat(user.createdAt()).isEqualTo(Instant.parse("2025-10-06T00:00:00Z"));
            })
            .anySatisfy((key, user) -> {
                assertThat(key).isEqualTo(2L);
                assertThat(user.name()).isEqualTo("Bob");
                assertThat(user.email()).isEqualTo("bob@example.com");
                assertThat(user.createdAt()).isEqualTo(Instant.parse("2025-10-06T01:00:00Z"));
            });
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