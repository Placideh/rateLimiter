package com.placideh.rateLimiter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private String id;
    private String status;
    private String type;
    private String recipient;
    private LocalDateTime sentAt;
    private String remainingRequests;
    private String message;

    // ============ GETTERS ============

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getRecipient() {
        return recipient;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public String getRemainingRequests() {
        return remainingRequests;
    }

    public String getMessage() {
        return message;
    }

    // ============ SETTERS ============

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setRemainingRequests(String remainingRequests) {
        this.remainingRequests = remainingRequests;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}