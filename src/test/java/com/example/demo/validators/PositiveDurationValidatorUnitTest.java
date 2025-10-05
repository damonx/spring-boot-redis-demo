package com.example.demo.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.example.demo.properties.RedisExtraProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.time.Duration;
import java.util.stream.Stream;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Unit test for {@link PositiveDurationValidator}.
 */
public class PositiveDurationValidatorUnitTest
{
    private PositiveDurationValidator positiveDurationValidator;
    private RedisExtraProperties redisExtraPropertiesUnderTest;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp()
    {
        positiveDurationValidator = new PositiveDurationValidator();
        redisExtraPropertiesUnderTest = new RedisExtraProperties();
    }

    @Test
    @DisplayName("Valid feature flag service cache ttl should return true.")
    void validFeatureFlagServiceCacheProperties()
    {
        // GIVEN
        redisExtraPropertiesUnderTest.setTtl(Duration.parse("PT5M"));
        // WHEN
        boolean isValid = positiveDurationValidator.isValid(redisExtraPropertiesUnderTest.getTtl(), context);
        // THEN
        assertThat(isValid).isTrue();
    }

    private static Stream<Arguments> nonPositiveDurationStrings()
    {
        return Stream.of(
            arguments(named("ZERO duration.", "PT0M")),
            arguments(named("Prefix negative duration.", "-PT5M")),
            arguments(named("Negative number duration.", "PT-5M"))
        );
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("nonPositiveDurationStrings")
    @DisplayName("Test invalid cache duration configurations, especially zero and negative duration.")
    void invalidCacheDurationConfigurations(final String duration)
    {
        // GIVEN
        redisExtraPropertiesUnderTest.setTtl(Duration.parse(duration));
        // WHEN
        boolean isValid = positiveDurationValidator.isValid(redisExtraPropertiesUnderTest.getTtl(), context);
        // THEN
        assertThat(isValid).isFalse();
    }
}
