package com.placideh.rateLimiter.service.rateLimit;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateLimitResult {

    private boolean allowed;
    private long currentUsage;
    private long limit;
    private long remainingRequests;
    private long retryAfterSeconds;
    private String limitType; // "WINDOW" or "MONTHLY"
    private String algorithmUsed;

    // Throttling information
    private String throttlingLevel;   // "NONE", "SOFT", "HARD"

    private String throttlingMessage;


    public boolean isAllowed() {
        return allowed;
    }

    public long getCurrentUsage() {
        return currentUsage;
    }

    public long getLimit() {
        return limit;
    }

    public long getRemainingRequests() {
        return remainingRequests;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public String getLimitType() {
        return limitType;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public String getThrottlingLevel() {
        return throttlingLevel;
    }

    public String getThrottlingMessage() {
        return throttlingMessage;
    }



    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public void setCurrentUsage(long currentUsage) {
        this.currentUsage = currentUsage;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void setRemainingRequests(long remainingRequests) {
        this.remainingRequests = remainingRequests;
    }

    public void setRetryAfterSeconds(long retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public void setThrottlingLevel(String throttlingLevel) {
        this.throttlingLevel = throttlingLevel;
    }

    public void setThrottlingMessage(String throttlingMessage) {
        this.throttlingMessage = throttlingMessage;
    }
}