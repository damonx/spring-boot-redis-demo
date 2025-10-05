package com.example.demo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "redis.spring.demo")
@Validated
public class RedisExtraProperties
{
    private Duration ttlInMinutes = Duration.ofMinutes(10) ;

    // Getters and Setters
    public Duration getTtlInMinutes()
    {
        return ttlInMinutes;
    }

    public void setTtlInMinutes(final Duration ttlInMinutes)
    {
        this.ttlInMinutes = ttlInMinutes;
    }
}
