package com.placideh.rateLimiter.repository;

import com.placideh.rateLimiter.model.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {

    Page<NotificationLog> findByClientId(String clientId, Pageable pageable);

    @Query("SELECT n FROM NotificationLog n WHERE n.clientId = :clientId AND n.sentAt >= :startDate")
    List<NotificationLog> findByClientIdAndSentAtAfter(String clientId, LocalDateTime startDate);

    @Query("SELECT n FROM NotificationLog n WHERE n.clientId = :clientId AND n.sentAt BETWEEN :startDate AND :endDate")
    List<NotificationLog> findByClientIdAndDateRange(String clientId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.clientId = :clientId AND n.notificationType = :type AND n.sentAt >= :startDate")
    long countByClientIdAndTypeAfterDate(String clientId, String type, LocalDateTime startDate);

    @Query("SELECT n FROM NotificationLog n WHERE n.status = :status")
    List<NotificationLog> findByStatus(String status);
}
