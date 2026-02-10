package com.ratelimiter.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchNotificationRequest {

    @NotEmpty(message = "Notifications list cannot be empty")
    @Valid
    private List<NotificationItem> notifications;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationItem {
        private String type; // SMS or EMAIL
        private String to;
        private String message; // For SMS
        private String subject; // For EMAIL
        private String body; // For EMAIL
    }
}
