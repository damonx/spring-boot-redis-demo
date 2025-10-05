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
    private final Map<Long, User> userDatabase = new HashMap<>(Map.of(
        1L, new User(1L, "Alice", "alice@example.com", Instant.now()),
        2L, new User(2L, "Bob", "bob@example.com", Instant.now())
    ));

    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        System.out.println("Fetching from DB: " + id);
        simulateLatency();
        return userDatabase.get(id);
    }

    @CachePut(value = "users", key = "#user.id")
    public User addUser(User user) {
        userDatabase.put(user.id(), user);
        return user;
    }

    @CacheEvict(value = "users", key = "#id")
    public void removeUser(Long id) {
        userDatabase.remove(id);
    }

    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        userDatabase.put(user.id(), user);
        return user;
    }

    public Map<Long, User> getAllUsers() {
        return Map.copyOf(userDatabase); // unmodifiable
    }

    private void simulateLatency() {
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
