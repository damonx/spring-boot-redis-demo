package com.example.demo.advice;

import com.example.demo.service.CacheMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Spring AOP advice (using {@link MethodInterceptor}) that wraps {@link Cacheable} methods
 * to record cache hit/miss metrics and logs cache eviction events.
 * <p>
 * This implementation uses pure Spring AOP (not AspectJ annotations).
 * </p>
 */
@Component
public class CacheMetricsAdvice implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheMetricsAdvice.class);

    private final CacheMetricsService cacheMetricsService;

    /**
     * Constructs the {@code CacheMetricsAdvice}.
     *
     * @param cacheMetricsService the metrics service that tracks cache hits and misses
     */
    public CacheMetricsAdvice(final CacheMetricsService cacheMetricsService) {
        this.cacheMetricsService = cacheMetricsService;
    }

    /**
     * Intercepts method calls on beans that match the configured pointcut.
     * <ul>
     *   <li>If the method is annotated with {@link Cacheable}, it records cache hits or misses.</li>
     *   <li>If the method is annotated with {@link CacheEvict}, it logs an eviction event.</li>
     * </ul>
     *
     * @param invocation the method invocation join point
     * @return the result of the method call
     * @throws Throwable if the target method throws an exception
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Object result = invocation.proceed();

        final Cacheable cacheable = invocation.getMethod().getAnnotation(Cacheable.class);
        final CacheEvict cacheEvict = invocation.getMethod().getAnnotation(CacheEvict.class);

        if (cacheable != null) {
            final String cacheName = cacheable.value().length > 0 ? cacheable.value()[0] : "default";
            final boolean isHit = result != null;

            if (isHit) {
                cacheMetricsService.incrementHitCount(cacheName);
                LOGGER.debug("Cache HIT for cache '{}'", cacheName);
            } else {
                cacheMetricsService.incrementMissCount(cacheName);
                LOGGER.debug("Cache MISS for cache '{}'", cacheName);
            }
        }

        if (cacheEvict != null) {
            LOGGER.info("Cache eviction triggered by {}", invocation.getMethod().getName());
        }
        return result;
    }
}
