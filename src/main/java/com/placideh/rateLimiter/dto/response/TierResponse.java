package com.placideh.rateLimiter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierResponse {

    private String id;
    private String name;
    private String description;
    private Integer requestsPerMinute;
    private Long requestsPerMonth;
    private BigDecimal pricePerMonth;
    private Boolean isActive;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public Long getRequestsPerMonth() {
        return requestsPerMonth;
    }

    public BigDecimal getPricePerMonth() {
        return pricePerMonth;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public void setRequestsPerMonth(Long requestsPerMonth) {
        this.requestsPerMonth = requestsPerMonth;
    }

    public void setPricePerMonth(BigDecimal pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}