package com.project.BankIt_backend.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){
        System.out.println("\nLOADED REDIS CONFIG\n");
        //setting default configurations
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.
                defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(
                                        new GenericJackson2JsonRedisSerializer()
                                )
                );

        // over-riding configurations for specific caches
        Map<String, RedisCacheConfiguration> specificConfigs = new HashMap<>();

        specificConfigs.put("data_analytics", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        specificConfigs.put("balance", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        specificConfigs.put("blacklisted_tokens", defaultConfig.entryTtl(Duration.ofMinutes(1)));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(specificConfigs)
                .build();
    }
}
