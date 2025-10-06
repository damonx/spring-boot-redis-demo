package com.example.demo.scheduler;

import com.example.demo.properties.RedisExtraProperties;
import com.example.demo.service.CacheRefreshService;
import com.example.demo.tracker.UserAccessTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

/**
 * Scheduler component responsible for proactively refreshing "hot" users in the cache.
 * <p>
 * This class periodically identifies the most frequently accessed users and triggers an
 * asynchronous refresh to keep their cache entries up-to-date. This helps improve
 * performance and reduces latency for frequently accessed data by applying a
 * refresh-ahead pattern.
 * </p>
 *
 * <p>
 * The scheduler relies on:
 * <ul>
 *     <li>{@link CacheRefreshService} to perform asynchronous cache refreshes.</li>
 *     <li>{@link UserAccessTracker} to determine which users are currently "hot" based on access frequency.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Refresh frequency is currently fixed at every {@code REFRESH_INTERVAL} (5 minutes),
 * and only the top {@code TOP_N_USERS} most frequently accessed users are refreshed.
 * </p>
 */
@Component
public class RefreshAheadScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshAheadScheduler.class);
    private static final String LOCK_KEY = "refresh_hot_users_lock";

    @Autowired
    private CacheRefreshService cacheRefreshService;
    @Autowired
    private UserAccessTracker userAccessTracker;
    @Autowired
    private RedisExtraProperties redisExtraProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Periodically refreshes the cache for the top {@code TOP_N_USERS} most frequently accessed users.
     * <p>
     * This method is scheduled to run every 5 minutes. It queries {@link UserAccessTracker}
     * for the hottest user IDs and invokes {@link CacheRefreshService#refreshUserAhead(Long)}
     * asynchronously for each.
     * </p>
     */
    @Scheduled(fixedRateString = "#{redisExtraProperties.hotUserInterval.toMillis()}")
    public void refreshHotUsers() {
        LOGGER.info("Current refresh hotUsers frequency is {} mins.", redisExtraProperties.getHotUserInterval().toMinutes());
        // Try to acquire lock (1 min expiration)
        final Boolean lock = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "locked", Duration.ofMinutes(1));
        if (lock == null || !lock) {
            LOGGER.info("Another instance is currently refreshing hot users. Skipping this cycle.");
            return; // another instance holds the lock
        }

        try {
            final Set<Long> hotUserIds = userAccessTracker.getTopHotUsers(redisExtraProperties.getTopNUsers());
            if (hotUserIds == null || hotUserIds.isEmpty()) {
                LOGGER.info("No hot users found to refresh at this time.");
                return;
            }

            LOGGER.info("Refreshing {} hot users: {}", hotUserIds.size(), hotUserIds);
            hotUserIds.forEach(userId -> {
                LOGGER.debug("Refreshing user with ID {}", userId);
                cacheRefreshService.refreshUserAhead(userId);
            });

        } finally {
            redisTemplate.delete(LOCK_KEY); // release lock
            LOGGER.debug("Released Redis lock for hot user refresh");
        }
    }

    /**
     * Runs once after the application is fully started.
     * Ensures Redis cache is pre-populated for hot users at startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCacheOnStartup() {
        LOGGER.info("Starting cache warm-up for hot users on startup");
        refreshHotUsers();
    }
}
