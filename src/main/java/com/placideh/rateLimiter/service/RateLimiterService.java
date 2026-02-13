package com.placideh.rateLimiter.service;


import com.placideh.rateLimiter.model.Tier;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.service.rateLimit.RateLimitResult;
import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Rate Limiter Service using Bucket4j + Redis
 *
 *
 * 1. Window time limits (per-minute)
 * 2. Monthly limits
 * 3.System-wide limits
 * 4. Distributed (Redis)
 * 5.Soft/Hard throttling
 */
@Slf4j
@Service
public class RateLimiterService {

    @Autowired
    private ProxyManager<String> proxyManager;

    @Value("${rate-limit.system.enabled:true}")
    private boolean systemWideEnabled;

    @Value("${rate-limit.system.requests-per-second:1000}")
    private long systemLimitPerSecond;

    @Value("${rate-limit.soft-delay-ms:500}")
    private long softDelayMs;

    /**
     * CHECK ALL LIMITS
     * Order: System-Wide → Window → Monthly
     */
    public RateLimitResult checkAllLimits(User user) {
        log.debug("Checking rate limits for user: {} (Tier: {})", user.getId(), user.getTier().getName());

        // check for SYSTEM WIDE
        if (systemWideEnabled) {
            RateLimitResult systemResult = checkSystemWideLimit();
            if (!systemResult.isAllowed()) {
                log.warn("SYSTEM-WIDE limit exceeded");
                return systemResult;
            }
        }

        // Window limit (per-minute)
        RateLimitResult windowResult = checkWindowLimit(user);
        if (!windowResult.isAllowed()) {
            log.warn("WINDOW limit exceeded for user: {}", user.getId());
            return windowResult;
        }

        // Monthly limit
        RateLimitResult monthlyResult = checkMonthlyLimit(user);
        if (!monthlyResult.isAllowed()) {
            log.warn("MONTHLY limit exceeded for user: {}", user.getId());
            return monthlyResult;
        }

        log.debug("All rate limit checks PASSED for user: {}", user.getId());
        return windowResult;
    }


    private RateLimitResult checkSystemWideLimit() {
        String bucketKey = "system:global";

        BucketConfiguration config = BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(systemLimitPerSecond, Duration.ofSeconds(1)))
                .build();

        Bucket bucket = proxyManager.builder().build(bucketKey, config);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        return RateLimitResult.builder()
                .allowed(probe.isConsumed())
                .currentUsage(systemLimitPerSecond - probe.getRemainingTokens())
                .limit(systemLimitPerSecond)
                .remainingRequests(probe.getRemainingTokens())
                .retryAfterSeconds(probe.isConsumed() ? 0 :
                        Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds())
                .limitType("SYSTEM_WIDE")
                .algorithmUsed("BUCKET4J")
                .throttlingLevel(probe.isConsumed() ? "NONE" : "HARD")
                .throttlingMessage(probe.isConsumed() ?
                        "System operating normally" :
                        "System capacity exceeded. Too many requests across all clients.")
                .build();
    }


    private RateLimitResult checkWindowLimit(User user) {
        Tier tier = user.getTier();
        long limit = tier.getRequestsPerMinute();

        String bucketKey = String.format("window:user:%s", user.getId());

        BucketConfiguration config = BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(limit, Duration.ofMinutes(1)))
                .build();

        Bucket bucket = proxyManager.builder().build(bucketKey, config);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // calculate throttling level
        long softThreshold = (long) (limit * 0.8); // 80%
        long current = limit - probe.getRemainingTokens();
        String throttlingLevel = determineThrottlingLevel(current, limit, softThreshold);

        // check for soft throttling
        if (!probe.isConsumed() && "SOFT".equalsIgnoreCase(tier.getThrottleMode())) {
            return handleSoftThrottle(user, bucketKey, config, tier, limit, current);
        }

        RateLimitResult rateLimitResult = new RateLimitResult();

        rateLimitResult.setAllowed(probe.isConsumed());
        rateLimitResult.setCurrentUsage(current);
        rateLimitResult.setLimit(limit);
        rateLimitResult.setRemainingRequests(probe.getRemainingTokens());
        rateLimitResult.setLimitType("WINDOW");
        rateLimitResult.setRetryAfterSeconds(probe.isConsumed() ? 0 :
                Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds());
        rateLimitResult.setAlgorithmUsed("BUCKET4J");
        rateLimitResult.setThrottlingLevel(throttlingLevel);
        rateLimitResult.setThrottlingMessage(getThrottlingMessage(throttlingLevel, current, limit));

        return rateLimitResult;
    }

    private RateLimitResult checkMonthlyLimit(User user) {
        Tier tier = user.getTier();
        long limit = tier.getRequestsPerMonth();

        String bucketKey = String.format("monthly:user:%s", user.getId());

        BucketConfiguration config = BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(limit, Duration.ofDays(30)))
                .build();

        Bucket bucket = proxyManager.builder().build(bucketKey, config);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        long softThreshold = (long) (limit * 0.9); // 90%
        long current = limit - probe.getRemainingTokens();
        String throttlingLevel = determineThrottlingLevel(current, limit, softThreshold);

        RateLimitResult rateLimitResult = new RateLimitResult();

        rateLimitResult.setAllowed(probe.isConsumed());
        rateLimitResult.setCurrentUsage(current);
        rateLimitResult.setLimit(limit);
        rateLimitResult.setRemainingRequests(probe.getRemainingTokens());
        rateLimitResult.setLimitType("MONTHLY");
        rateLimitResult.setRetryAfterSeconds(0);
        rateLimitResult.setAlgorithmUsed("BUCKET4J");
        rateLimitResult.setRetryAfterSeconds(0);
        rateLimitResult.setThrottlingLevel(throttlingLevel);
        rateLimitResult.setThrottlingMessage(getThrottlingMessage(throttlingLevel, current, limit));

        return rateLimitResult;
    }


    private RateLimitResult handleSoftThrottle(User user, String bucketKey,
                                               BucketConfiguration config,
                                               Tier tier, long limit, long current) {
        try {
            log.info("SOFT throttle: Delaying user {} for {}ms", user.getId(), softDelayMs);
            Thread.sleep(softDelayMs);

            // Retry after delay
            Bucket retryBucket = proxyManager.builder().build(bucketKey, config);
            ConsumptionProbe retryProbe = retryBucket.tryConsumeAndReturnRemaining(1);

            if (retryProbe.isConsumed()) {
                log.info("SOFT throttle SUCCESS: Request allowed after delay for user {}", user.getId());

                RateLimitResult rateLimitResult = new RateLimitResult();

                rateLimitResult.setAllowed(true);
                rateLimitResult.setCurrentUsage(limit - retryProbe.getRemainingTokens());
                rateLimitResult.setLimit(limit);
                rateLimitResult.setRemainingRequests(retryProbe.getRemainingTokens());
                rateLimitResult.setLimitType("WINDOW");
                rateLimitResult.setAlgorithmUsed("BUCKET4J");
                rateLimitResult.setThrottlingLevel("SOFT");
                rateLimitResult.setThrottlingMessage("Request allowed after soft throttle delay");

                return rateLimitResult;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Soft throttle failed - return hard block
        RateLimitResult rateLimitResult = new RateLimitResult();

        rateLimitResult.setAllowed(true);
        rateLimitResult.setCurrentUsage(current);
        rateLimitResult.setLimit(limit);
        rateLimitResult.setRemainingRequests(0);
        rateLimitResult.setRetryAfterSeconds(60);
        rateLimitResult.setLimitType("WINDOW");
        rateLimitResult.setAlgorithmUsed("BUCKET4J");
        rateLimitResult.setThrottlingLevel("HARD");
        rateLimitResult.setThrottlingMessage("Rate limit exceeded even after soft throttle. Retry after 60 seconds.");


        return rateLimitResult;
    }


    private String determineThrottlingLevel(long current, long limit, long softThreshold) {
        if (current >= limit) {
            return "HARD";
        } else if (current >= softThreshold) {
            return "SOFT";
        } else {
            return "NONE";
        }
    }


    private String getThrottlingMessage(String level, long current, long limit) {
        switch (level) {
            case "HARD":
                return String.format("Rate limit exceeded (%d/%d). Request blocked.", current, limit);
            case "SOFT":
                double percentage = (current * 100.0) / limit;
                return String.format("Warning: Approaching limit (%.1f%%). Consider slowing down.", percentage);
            default:
                return "Normal operation";
        }
    }
}