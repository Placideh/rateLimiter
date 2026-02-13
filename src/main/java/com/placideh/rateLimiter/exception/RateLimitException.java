package com.placideh.rateLimiter.exception;



public class RateLimitException extends RuntimeException {

    private  String limitType;
    private  long currentUsage;
    private  long limit;
    private  long retryAfterSeconds;

    public RateLimitException(String message, String limitType, long currentUsage, long limit, long retryAfterSeconds) {
        super(message);
        this.limitType = limitType;
        this.currentUsage = currentUsage;
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }


    public  String getLimitType(){
        return this.limitType;
    }

    public Long getCurrentUsage(){
        return this.currentUsage;
    }

    public Long getLimit(){
        return this.limit;
    }

    public Long getRetryAfterSeconds(){
        return this.retryAfterSeconds;
    }


    public void setLimitType(String limitType){
        this.limitType = limitType;
    }

    public void setLimit(long limit){
        this.limit = limit;
    }

    public void setCurrentUsage(long currentUsage){
        this.currentUsage =currentUsage;
    }

    public void setRetryAfterSeconds(long retryAfterSeconds){
        this.retryAfterSeconds = retryAfterSeconds;
    }



}
