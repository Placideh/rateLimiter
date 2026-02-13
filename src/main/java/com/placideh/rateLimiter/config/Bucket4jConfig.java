package com.placideh.rateLimiter.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Bucket4j Configuration with Redis
 * Includes expiration strategy for rate limit buckets
 */
@Slf4j
@Configuration
public class Bucket4jConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    public RedisClient redisClient() {
        String redisUri = String.format("redis://%s:%d", redisHost, redisPort);

        log.info("Connecting to Redis at {}:{}", redisHost, redisPort);

        try {
            RedisClient client = RedisClient.create(redisUri);
            log.info("Redis client created successfully");
            return client;
        } catch (Exception e) {
            log.error("Failed to create Redis client: {}", e.getMessage());
            log.error("Verify Redis is running: brew services list | grep redis");
            throw new RuntimeException("Redis client creation failed", e);
        }
    }


    @Bean
    public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        log.info("Creating Redis connection...");

        try {
            RedisCodec<String, byte[]> codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
            StatefulRedisConnection<String, byte[]> connection = redisClient.connect(codec);

            // test the connection
            String pingResult = connection.sync().ping();
            log.info("Redis connection established - PING response: {}", pingResult);

            return connection;
        } catch (Exception e) {
            log.error("Failed to connect to Redis at {}:{}", redisHost, redisPort);
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException("Redis connection failed", e);
        }
    }


    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> connection) {
        log.info("Creating Bucket4j ProxyManager with expiration strategy...");

        try {
            // creating expiration strategy: buckets expire 1 hour after last write
            ExpirationAfterWriteStrategy expirationStrategy =
                    ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1));

            ProxyManager<String> proxyManager = LettuceBasedProxyManager.builderFor(connection)
                    .withExpirationStrategy(expirationStrategy)
                    .build();

            log.info("Bucket4j ProxyManager created successfully");
            log.info("Expiration Strategy: Buckets expire after 1 hour of inactivity");

            return proxyManager;
        } catch (Exception e) {
            log.error("Failed to create ProxyManager: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("ProxyManager creation failed", e);
        }
    }
}