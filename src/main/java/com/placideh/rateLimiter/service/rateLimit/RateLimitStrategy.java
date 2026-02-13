package com.placideh.rateLimiter.service.rateLimit;


import com.placideh.rateLimiter.model.User;

public interface RateLimitStrategy {

    RateLimitResult checkRateLimit(User client);

    String getStrategyName();
}
