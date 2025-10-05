package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    // Simulate a database
    private static final Map<Long, User> USER_DATABASE = new HashMap<>(Map.of(
        1L, new User(1L, "Alice", "alice@example.com", Instant.parse("2025-10-06T00:00:00Z")),
        2L, new User(2L, "Bob", "bob@example.com", Instant.parse("2025-10-06T00:05:00Z"))
    ));

    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        System.out.println("Fetching from DB: " + id);
        simulateLatency();
        return USER_DATABASE.get(id);
    }

    @CachePut(value = "users", key = "#user.id")
    public User addUser(User user) {
        USER_DATABASE.put(user.id(), user);
        return user;
    }

    @CacheEvict(value = "users", key = "#id")
    public void removeUser(Long id) {
        USER_DATABASE.remove(id);
    }

    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        USER_DATABASE.put(user.id(), user);
        return user;
    }

    /**
     * Clears the entire "users" cache and the simulated database.
     * @CacheEvict(value = "users", allEntries = true) ensures all cached user objects are removed.
     */
    @CacheEvict(value = "users", allEntries = true)
    public void removeAllUsers() {
        USER_DATABASE.clear();
    }

    public Map<Long, User> getAllUsers() {
        return Map.copyOf(USER_DATABASE);
    }

    private void simulateLatency() {
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
