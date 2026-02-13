package com.placideh.rateLimiter.exception;





public class RateLimitExceededException extends RuntimeException {

    private final String limitType;       // "WINDOW", "MONTHLY", "SYSTEM_WIDE"
    private final long currentUsage;
    private final long limit;
    private final long retryAfterSeconds;

    public RateLimitExceededException(String limitType, long currentUsage, long limit,
                                      long retryAfterSeconds, String message) {
        super(message);
        this.limitType = limitType;
        this.currentUsage = currentUsage;
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String getLimitType() {
        return limitType;
    }

    public long getCurrentUsage() {
        return currentUsage;
    }

    public long getLimit() {
        return limit;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
