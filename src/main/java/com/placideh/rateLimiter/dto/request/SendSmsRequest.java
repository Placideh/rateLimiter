package com.placideh.rateLimiter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public class SendSmsRequest {

    @NotBlank(message = "Recipient phone number is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format. Must be format like +250781234567"
    )
    private String to;

    @NotBlank(message = "Message is required")
    private String message;

    public SendSmsRequest(String to, String message){
        this.message=message;
        this.to=to;

    }


    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }


    public void setTo(String to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}