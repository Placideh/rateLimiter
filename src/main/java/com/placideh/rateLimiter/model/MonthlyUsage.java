package com.placideh.rateLimiter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_usage",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year_month"}))
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MonthlyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth; // Format: YYYY-MM

    @Column(name = "total_requests")
    private Long totalRequests = 0L;

    @Column(name = "sms_count")
    private Long smsCount = 0L;

    @Column(name = "email_count")
    private Long emailCount = 0L;

    @Column(name = "rate_limit_hits")
    private Integer rateLimitHits = 0;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;


    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getYearMonth() {
        return yearMonth;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
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

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}