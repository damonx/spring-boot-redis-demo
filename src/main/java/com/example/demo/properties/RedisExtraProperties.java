package com.example.demo.properties;

import com.example.demo.validators.PositiveDuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@ConfigurationProperties(prefix = "redis.spring.demo")
@Validated
public class RedisExtraProperties {

    @PositiveDuration
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration ttl;

    // Getters and Setters
    public Duration getTtl()
    {
        return ttl;
    }

    public void setTtl(final Duration ttl)
    {
        this.ttl = ttl;
    }
}
