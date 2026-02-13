package com.placideh.rateLimiter.dto.request;

import jakarta.validation.constraints.NotBlank;



public class UpdateUserTierRequest {

    @NotBlank(message = "Tier ID is required")
    private String tierId;


    public void setTierId(String tierId) {
        this.tierId = tierId;
    }

    public String getTierId() {
        return tierId;
    }
}