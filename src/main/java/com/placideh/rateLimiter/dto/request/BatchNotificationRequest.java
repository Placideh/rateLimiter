package com.placideh.rateLimiter.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class BatchNotificationRequest {

    @NotEmpty(message = "Notifications list cannot be empty")
    @Valid
    private List<NotificationItem> notifications;

    public List<NotificationItem> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationItem {
        private String type; // SMS or EMAIL
        private String to;
        private String message; // For SMS
        private String subject; // For EMAIL
        private String body; // For EMAIL


        public String getType() {
            return type;
        }

        public String getTo() {
            return to;
        }

        public String getMessage() {
            return message;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}