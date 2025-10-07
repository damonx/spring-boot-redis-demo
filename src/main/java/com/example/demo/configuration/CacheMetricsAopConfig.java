package com.example.demo.configuration;

import com.example.demo.advice.CacheMetricsAdvice;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.Pointcuts;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AOP configuration that applies {@link CacheMetricsAdvice}
 * to methods annotated with {@link Cacheable} or {@link CacheEvict}.
 */
@Configuration
public class CacheMetricsAopConfig {

    /**
     * Ensures Spring auto-proxies beans that match advisors.
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * Defines a pointcut that matches any method annotated with
     * {@link Cacheable} or {@link CacheEvict}.
     */
    @Bean
    public Pointcut cacheAnnotationPointcut() {
        final Pointcut cacheable = new AnnotationMatchingPointcut(null, Cacheable.class, true);
        final Pointcut cacheEvict = new AnnotationMatchingPointcut(null, CacheEvict.class, true);

        return Pointcuts.union(cacheable, cacheEvict);
    }

    /**
     * Creates the advisor that applies {@link CacheMetricsAdvice} to the cache-related methods.
     *
     * @param advice the {@link CacheMetricsAdvice} interceptor
     * @return a configured {@link Advisor}
     */
    @Bean
    public Advisor cacheMetricsAdvisor(CacheMetricsAdvice advice) {
        return new DefaultPointcutAdvisor(cacheAnnotationPointcut(), advice);
    }
}