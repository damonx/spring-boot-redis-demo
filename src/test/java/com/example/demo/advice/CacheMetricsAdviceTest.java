package com.example.demo.advice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.demo.service.CacheMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for {@link CacheMetricsAdvice}.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CacheMetricsAdviceTest.TestConfig.class)
class CacheMetricsAdviceTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public CacheMetricsService cacheMetricsService() {
            return mock(CacheMetricsService.class);
        }

        @Bean
        public CacheMetricsAdvice cacheMetricsAdvice(CacheMetricsService service) {
            return new CacheMetricsAdvice(service);
        }

        @Bean
        public DefaultAdvisorAutoProxyCreator autoProxyCreator() {
            return new DefaultAdvisorAutoProxyCreator();
        }

        @Bean
        public Pointcut cacheablePointcut() {
            // Matches methods annotated with @Cacheable or @CacheEvict
            return new AnnotationMatchingPointcut(null, Cacheable.class, false);
        }

        @Bean
        public Pointcut cacheEvictPointcut() {
            return new AnnotationMatchingPointcut(null, CacheEvict.class, false);
        }

        @Bean
        public Advisor cacheableAdvisor(CacheMetricsAdvice advice) {
            return new DefaultPointcutAdvisor(cacheablePointcut(), advice);
        }

        @Bean
        public Advisor cacheEvictAdvisor(CacheMetricsAdvice advice) {
            return new DefaultPointcutAdvisor(cacheEvictPointcut(), advice);
        }

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    @Component
    static class TestService {

        @Cacheable("users")
        public String cachedMethodReturningValue() {
            return "user-123";
        }

        @Cacheable("users")
        public String cachedMethodReturningNull() {
            return null;
        }

        @CacheEvict("users")
        public void evictCacheMethod() {
            // No-op; only for testing eviction
        }
    }

    @Autowired
    private CacheMetricsService cacheMetricsService;

    @Autowired
    private TestService testService;

    @BeforeEach
    void resetMocks() {
        reset(cacheMetricsService);
    }

    @Test
    @DisplayName("Should increment hit count when @Cacheable method returns non-null value")
    void testCacheHitRecorded() {
        testService.cachedMethodReturningValue();

        verify(cacheMetricsService, times(1)).incrementHitCount("users");
        verify(cacheMetricsService, never()).incrementMissCount("users");
    }

    @Test
    @DisplayName("Should increment miss count when @Cacheable method returns null")
    void testCacheMissRecorded() {
        testService.cachedMethodReturningNull();

        verify(cacheMetricsService, times(1)).incrementMissCount("users");
        verify(cacheMetricsService, never()).incrementHitCount("users");
    }

    @Test
    @DisplayName("Should log eviction when @CacheEvict method is invoked")
    void testCacheEvictionLogged() {
        testService.evictCacheMethod();
        // We can’t easily verify the log, but this ensures no exception is thrown
        verifyNoInteractions(cacheMetricsService);
    }
}