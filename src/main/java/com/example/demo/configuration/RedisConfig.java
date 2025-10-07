package com.example.demo.configuration;

import com.example.demo.model.User;
import com.example.demo.properties.RedisExtraProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * A simple configuration for Redis.
 */
@Configuration
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(final RedisProperties redisProperties) {
        return new LettuceConnectionFactory(
            redisProperties.getHost(),
            redisProperties.getPort()
        );
    }

    @Bean
    public RedisCacheManager cacheManager(final LettuceConnectionFactory redisConnectionFactory, final RedisExtraProperties redisExtraProperties) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        final Jackson2JsonRedisSerializer<User> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, User.class);

        final RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(redisExtraProperties.getTtl())
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build();
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate(final LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 1. Set Key Serializer (For the ZSet Name: "hotUsers")
        // Use String serializer for keys
        template.setKeySerializer(stringSerializer);

        // 2. Set Value Serializer (For ZSet Members: Long userId)
        // The general ValueSerializer is used for ZSet members.
        // Converting the Long userId to a String for visibility in Redis is best.
        template.setValueSerializer(stringSerializer);

        // Since ZSet operations in the tracker are on the root template,
        // these two general settings cover the ZSet key and member serialization.

        template.afterPropertiesSet();
        return template;
    }
}
