package com.placideh.rateLimiter.service;


import com.placideh.rateLimiter.dto.response.UsageResponse;
import com.placideh.rateLimiter.exception.ResourceNotFoundException;
import com.placideh.rateLimiter.model.MonthlyUsage;
import com.placideh.rateLimiter.model.Tier;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.repository.MonthlyUsageRepository;
import com.placideh.rateLimiter.repository.UserRepository;
import com.placideh.rateLimiter.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsageService {



    @Autowired
    private UserRepository clientRepository;

    @Autowired
    private MonthlyUsageRepository monthlyUsageRepository;

    public UsageResponse getCurrentUsage(String clientId) {
        User client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        String currentMonth = DateTimeUtil.getCurrentYearMonth();
        MonthlyUsage usage = monthlyUsageRepository
                .findByUserIdAndYearMonth(clientId, currentMonth)
                .orElse(new MonthlyUsage());

        Tier tier = client.getTier();
        long totalRequests = usage.getTotalRequests() != null ? usage.getTotalRequests() : 0;
        long remainingRequests = tier.getRequestsPerMonth() - totalRequests;
        double percentageUsed = (totalRequests * 100.0) / tier.getRequestsPerMonth();

        UsageResponse usageResponse = new UsageResponse();
        usageResponse.setCurrentMonth(currentMonth);
        usageResponse.setClientId(clientId);
        usageResponse.setClientName(client.getUsername());
        usageResponse.setSmsCount(usage.getSmsCount() != null ? usage.getSmsCount() : 0);
        usageResponse.setTierName(tier.getName());
        usageResponse.setTotalRequests(totalRequests);
        usageResponse.setEmailCount(usage.getEmailCount() != null ? usage.getEmailCount() : 0);
        usageResponse.setRateLimitHits(usage.getRateLimitHits() !=null ? usage.getRateLimitHits() : 0);
        usageResponse.setRequestsPerMonth(tier.getRequestsPerMonth());
        usageResponse.setRequestsPerMinute(tier.getRequestsPerMinute());
        usageResponse.setRemainingRequests(Math.max(0,remainingRequests));
        usageResponse.setPercentageUsed(Math.min(100.0,percentageUsed));

        return usageResponse;
    }

    public List<UsageResponse> getUsageHistory(String clientId, int months) {
        User client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        List<MonthlyUsage> usageList = monthlyUsageRepository
                .findByUserIdOrderByYearMonthDesc(clientId);

        return usageList.stream()
                .limit(months)
                .map(usage -> mapToResponse(client, usage))
                .collect(Collectors.toList());
    }

    public List<UsageResponse> getAllClientsUsage() {
        String currentMonth = DateTimeUtil.getCurrentYearMonth();
        List<MonthlyUsage> allUsage = monthlyUsageRepository.findByYearMonth(currentMonth);

        return allUsage.stream()
                .map(usage -> {
                    User client = clientRepository.findById(usage.getUserId())
                            .orElse(null);
                    return client != null ? mapToResponse(client, usage) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    private UsageResponse mapToResponse(User client, MonthlyUsage usage) {
        Tier tier = client.getTier();
        long totalRequests = usage.getTotalRequests() != null ? usage.getTotalRequests() : 0;
        long remainingRequests = tier.getRequestsPerMonth() - totalRequests;
        double percentageUsed = (totalRequests * 100.0) / tier.getRequestsPerMonth();

        UsageResponse usageResponse = new UsageResponse();
        usageResponse.setClientId(client.getId());
        usageResponse.setClientName(client.getUsername());
        usageResponse.setTierName(tier.getName());
        usageResponse.setCurrentMonth(usage.getYearMonth());
        usageResponse.setTotalRequests(totalRequests);
        usageResponse.setSmsCount(usage.getSmsCount() !=null ? usage.getSmsCount() : 0);
        usageResponse.setEmailCount(usage.getEmailCount() !=null ? usage.getEmailCount() : 0);
        usageResponse.setRateLimitHits(usage.getRateLimitHits() !=null ? usage.getRateLimitHits() : 0);
        usageResponse.setRequestsPerMonth(tier.getRequestsPerMonth());
        usageResponse.setRequestsPerMinute(tier.getRequestsPerMinute());
        usageResponse.setRemainingRequests(Math.max(0, remainingRequests));
        usageResponse.setPercentageUsed(Math.min(100.0, percentageUsed));



        return usageResponse;
    }
}
