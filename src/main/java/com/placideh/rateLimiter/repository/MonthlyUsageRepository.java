package com.placideh.rateLimiter.repository;


import com.placideh.rateLimiter.model.MonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyUsageRepository extends JpaRepository<MonthlyUsage, String> {

    Optional<MonthlyUsage> findByUserIdAndYearMonth(String userId, String yearMonth);

    List<MonthlyUsage> findByUserId(String userId);

    List<MonthlyUsage> findByYearMonth(String yearMonth);

    @Query("SELECT m FROM MonthlyUsage m WHERE m.userId = :userId ORDER BY m.yearMonth DESC")
    List<MonthlyUsage> findByUserIdOrderByYearMonthDesc(String userId);

    @Query("SELECT SUM(m.totalRequests) FROM MonthlyUsage m WHERE m.yearMonth = :yearMonth")
    Long getTotalRequestsForMonth(String yearMonth);
}
