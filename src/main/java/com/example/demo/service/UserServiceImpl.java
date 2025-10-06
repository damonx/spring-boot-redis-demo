package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.tracker.UserAccessTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserAccessTracker userAccessTracker;

    // Simulate a database
    private static final Map<Long, User> USER_DATABASE = new HashMap<>(Map.of(
        1L, new User(1L, "Alice", "alice@example.com", Instant.parse("2025-10-06T00:00:00Z")),
        2L, new User(2L, "Bob", "bob@example.com", Instant.parse("2025-10-06T00:05:00Z"))
    ));

    @Cacheable(cacheNames = "users", key = "#id", sync = true)
    public User getUserById(final Long id)
    {
        LOGGER.debug("Fetching user from DB with ID: {}", id);
        simulateLatency();
        final User user = USER_DATABASE.get(id);
        if  (user != null) {
            userAccessTracker.recordAccess(id);
        }
        return user;
    }

    @Override
    public User getUserByIdBypassCache(final Long id)
    {
        LOGGER.debug("Fetching user by ID bypassing cache: {}", id);
        simulateLatency();
        final User user = USER_DATABASE.get(id);
        if (user != null) {
            userAccessTracker.recordAccess(id); // Track access
        }
        return user;
    }

    // Refresh cache (fetch + update cache)
    @CachePut(cacheNames = "users", key = "#id")
    public void refreshUser(Long id) {
        LOGGER.info("Refreshing user in cache: {}", id);
        getUserByIdBypassCache(id); // fetch fresh data
    }

    @CachePut(value = "users", key = "#user.id")
    public User addUser(User user)
    {
        LOGGER.info("Adding new user to database and cache: {}", user.id());
        USER_DATABASE.put(user.id(), user);
        return user;
    }

    @CacheEvict(value = "users", key = "#id")
    public void removeUser(Long id)
    {
        LOGGER.info("Removing user from database and cache: {}", id);
        USER_DATABASE.remove(id);
    }

    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user)
    {
        LOGGER.info("Updating user in database and cache: {}", user.id());
        USER_DATABASE.put(user.id(), user);
        return user;
    }

    /**
     * Clears the entire "users" cache and the simulated database.
     *
     * @CacheEvict(value = "users", allEntries = true) ensures all cached user objects are removed.
     */
    @CacheEvict(value = "users", allEntries = true)
    public void removeAllUsers()
    {
        LOGGER.info("Removing all users from database and cache");
        USER_DATABASE.clear();
    }

    public Map<Long, User> getAllUsers()
    {
        LOGGER.debug("Fetching all users from database");
        return Map.copyOf(USER_DATABASE);
    }

    private void simulateLatency()
    {
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
