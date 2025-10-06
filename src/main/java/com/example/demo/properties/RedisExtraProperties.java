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

    /** Interval for refreshing hot users in the cache */
    @PositiveDuration
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration hotUserInterval;

    private int topNUsers = 10;

    // Getters and Setters
    public Duration getTtl()
    {
        return ttl;
    }

    public void setTtl(final Duration ttl)
    {
        this.ttl = ttl;
    }

    public Duration getHotUserInterval() {
        return hotUserInterval;
    }

    public void setHotUserInterval(Duration hotUserInterval) {
        this.hotUserInterval = hotUserInterval;
    }

    public int getTopNUsers()
    {
        return topNUsers;
    }

    public void setTopNUsers(final int topNUsers)
    {
        this.topNUsers = topNUsers;
    }
}
