package com.innowise.userservice.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innowise.userservice.dto.UserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @ClassName RedisConfig
 * @Description Configuration class for Redis caching.
 * @Author dshparko
 * @Date 13.09.2025 10:53
 * @Version 1.0
 */
@Configuration
public class RedisConfig {
    /**
     * Creates and configures a {@link RedisCacheManager} for managing application-level caches.
     * Applies the custom {@link RedisCacheConfiguration} defined in {@link #cacheConfiguration()} as the default
     * for all caches used via Spring's caching annotations ({@code @Cacheable}, {@code @CacheEvict}, etc.).
     *
     * @param connectionFactory Redis connection factory used to communicate with the Redis server
     * @return configured {@link RedisCacheManager} instance with default cache behavior
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration())
                .build();
    }

    /**
     * Defines the default Redis cache configuration.
     * - Sets TTL to 60 minutes for all cache entries.
     * - Disables caching of null values.
     * - Serializes keys as UTF-8 strings.
     * - Serializes values using a type-safe JSON serializer with embedded type metadata.
     *
     * @return configured {@link RedisCacheConfiguration} instance
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        Jackson2JsonRedisSerializer<UserDto> serializer =
                new Jackson2JsonRedisSerializer<>(redisObjectMapper(), UserDto.class);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    /**
     * Creates a customized {@link ObjectMapper} for Redis serialization.
     * - Enables case-insensitive property matching.
     * - Ignores unknown fields during deserialization.
     * - Supports Java 8 date/time types via {@link JavaTimeModule}.
     * - Excludes null values from JSON output.
     * - Accepts single values as arrays for flexible deserialization.
     *
     * @return configured {@link ObjectMapper} instance
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        return JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build()
                .registerModule(new JavaTimeModule())
                .registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module())
                .registerModule(new com.fasterxml.jackson.module.paramnames.ParameterNamesModule());
    }
}
