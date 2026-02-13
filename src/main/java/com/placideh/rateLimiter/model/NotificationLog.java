package com.placideh.rateLimiter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "notification_type", nullable = false, length = 20)
    private String notificationType; // SMS or EMAIL

    @Column(nullable = false)
    private String recipient;

    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;

    @Column(length = 500)
    private String subject; // For emails

    @Column(name = "algorithm_used", nullable = false, length = 50)
    private String algorithmUsed; // TOKEN_BUCKET or FIXED_WINDOW

    @Column(name = "tier_at_send", length = 100)
    private String tierAtSend;

    @Column(length = 20)
    private String status = "SENT"; // SENT, FAILED, QUEUED

    @CreatedDate
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;


    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getSubject() {
        return subject;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public String getTierAtSend() {
        return tierAtSend;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public void setTierAtSend(String tierAtSend) {
        this.tierAtSend = tierAtSend;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}