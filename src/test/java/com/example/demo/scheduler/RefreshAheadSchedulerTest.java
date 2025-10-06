package com.example.demo.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import com.example.demo.properties.RedisExtraProperties;
import com.example.demo.service.CacheRefreshService;
import com.example.demo.tracker.UserAccessTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class RefreshAheadSchedulerTest {
    @Mock
    private CacheRefreshService cacheRefreshService;
    @Mock
    private UserAccessTracker userAccessTracker;
    @Mock
    private RedisExtraProperties redisExtraProperties;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @InjectMocks
    private RefreshAheadScheduler refreshAheadScheduler;

    @BeforeEach
    void setUp() {
        when(redisExtraProperties.getHotUserInterval()).thenReturn(Duration.parse("PT5M"));
        when(redisExtraProperties.getTopNUsers()).thenReturn(10);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("Test refresh hot users should call refreshUser for each hot user.")
    void refreshHotUsers_shouldCallRefreshForEachHotUser() {
        // GIVEN
        final Set<Long> hotUserIds = Set.of(10L, 20L, 30L);
        when(userAccessTracker.getTopHotUsers(10)).thenReturn(hotUserIds);

        // WHEN
        refreshAheadScheduler.refreshHotUsers();

        // THEN
        verify(cacheRefreshService).refreshUserAhead(10L);
        verify(cacheRefreshService).refreshUserAhead(20L);
        verify(cacheRefreshService).refreshUserAhead(30L);
        verify(cacheRefreshService, times(3)).refreshUserAhead(anyLong());
    }

    @Test
    @DisplayName("Test refresh hot user does nothing when there are no hot users.")
    void refreshHotUsers_shouldDoNothingWhenNoHotUsers() {
        // GIVEN
        when(userAccessTracker.getTopHotUsers(10)).thenReturn(Set.of());
        // WHEN
        refreshAheadScheduler.refreshHotUsers();
        // THEN
        verify(cacheRefreshService, never()).refreshUserAhead(anyLong());
    }
}
