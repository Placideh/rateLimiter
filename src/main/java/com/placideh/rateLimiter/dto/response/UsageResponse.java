package com.placideh.rateLimiter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageResponse {

    private String clientId;
    private String clientName;
    private String tierName;
    private String currentMonth;
    private Long totalRequests;
    private Long smsCount;
    private Long emailCount;
    private Integer rateLimitHits;
    private Integer requestsPerMinute;
    private Long requestsPerMonth;
    private Long remainingRequests;
    private Double percentageUsed;


    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getTierName() {
        return tierName;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public Long getSmsCount() {
        return smsCount;
    }

    public Long getEmailCount() {
        return emailCount;
    }

    public Integer getRateLimitHits() {
        return rateLimitHits;
    }

    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public Long getRequestsPerMonth() {
        return requestsPerMonth;
    }

    public Long getRemainingRequests() {
        return remainingRequests;
    }

    public Double getPercentageUsed() {
        return percentageUsed;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public void setSmsCount(Long smsCount) {
        this.smsCount = smsCount;
    }

    public void setEmailCount(Long emailCount) {
        this.emailCount = emailCount;
    }

    public void setRateLimitHits(Integer rateLimitHits) {
        this.rateLimitHits = rateLimitHits;
    }

    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public void setRequestsPerMonth(Long requestsPerMonth) {
        this.requestsPerMonth = requestsPerMonth;
    }

    public void setRemainingRequests(Long remainingRequests) {
        this.remainingRequests = remainingRequests;
    }

    public void setPercentageUsed(Double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }
}