package com.placideh.rateLimiter.service;

import com.placideh.rateLimiter.dto.request.BatchNotificationRequest;
import com.placideh.rateLimiter.dto.request.SendEmailRequest;
import com.placideh.rateLimiter.dto.request.SendSmsRequest;
import com.placideh.rateLimiter.dto.response.NotificationResponse;
import com.placideh.rateLimiter.exception.RateLimitExceededException;
import com.placideh.rateLimiter.model.NotificationLog;
import com.placideh.rateLimiter.model.SystemConfig;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.repository.NotificationLogRepository;
import com.placideh.rateLimiter.repository.SystemConfigRepository;
import com.placideh.rateLimiter.service.rateLimit.RateLimitResult;
import com.placideh.rateLimiter.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notification Service with BUILT-IN Rate Limiting
 *
 * Rate limiting happens HERE before sending any notification
 * This ensures ALL calls (HTTP, scheduled jobs, queues) are rate limited
 */
@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private RateLimiterService rateLimiterService;

    /**
     * Send SMS with rate limiting
     *
     * @param client User sending the SMS
     * @param request SMS request
     * @return Notification response
     * @throws RateLimitExceededException if rate limit exceeded
     */
    public NotificationResponse sendSms(User client, SendSmsRequest request) {
        log.info("SMS request from user: {} (Tier: {}) to: {}",
                client.getId(), client.getTier().getName(), request.getTo());

        RateLimitResult rateLimitResult = rateLimiterService.checkAllLimits(client);

        if (!rateLimitResult.isAllowed()) {
            log.warn("SMS BLOCKED - Rate limit exceeded for user {}: {} (Type: {}, Usage: {}/{})",
                    client.getId(),
                    rateLimitResult.getThrottlingMessage(),
                    rateLimitResult.getLimitType(),
                    rateLimitResult.getCurrentUsage(),
                    rateLimitResult.getLimit());

            throw new RateLimitExceededException(
                    rateLimitResult.getLimitType(),
                    rateLimitResult.getCurrentUsage(),
                    rateLimitResult.getLimit(),
                    rateLimitResult.getRetryAfterSeconds(),
                    rateLimitResult.getThrottlingMessage()
            );
        }


        // get current algorithm
        String algorithm = getCurrentAlgorithm();

        NotificationLog log = new NotificationLog();
        log.setClientId(client.getId());
        log.setNotificationType(Constants.NOTIFICATION_SMS);
        log.setRecipient(request.getTo());
        log.setMessageContent(request.getMessage());
        log.setAlgorithmUsed(algorithm);
        log.setTierAtSend(client.getTier().getName());
        log.setSentAt(LocalDateTime.now());
        log.setStatus(Constants.NOTIF_STATUS_SENT);
        log = notificationLogRepository.save(log);


        System.out.println("SMS sent to: " + request.getTo() + " - Message: " + request.getMessage());


        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setId(log.getId());
        notificationResponse.setStatus(Constants.NOTIF_STATUS_SENT);
        notificationResponse.setType(Constants.NOTIFICATION_SMS);
        notificationResponse.setRecipient(request.getTo());
        notificationResponse.setSentAt(log.getSentAt());
        notificationResponse.setMessage("SMS sent successfully");

        return notificationResponse;
    }

 // send Email with rate limiting
    public NotificationResponse sendEmail(User client, SendEmailRequest request) {
        log.info("Email request from user: {} (Tier: {}) to: {}",
                client.getId(), client.getTier().getName(), request.getTo());

        RateLimitResult rateLimitResult = rateLimiterService.checkAllLimits(client);

        if (!rateLimitResult.isAllowed()) {
            log.warn("EMAIL BLOCKED - Rate limit exceeded for user {}: {} (Type: {}, Usage: {}/{})",
                    client.getId(),
                    rateLimitResult.getThrottlingMessage(),
                    rateLimitResult.getLimitType(),
                    rateLimitResult.getCurrentUsage(),
                    rateLimitResult.getLimit());

            logFailedNotification(client, Constants.NOTIFICATION_EMAIL, request.getTo(),
                    request.getBody(), request.getSubject(), "Rate limit exceeded: " + rateLimitResult.getThrottlingMessage());

            throw new RateLimitExceededException(
                    rateLimitResult.getLimitType(),
                    rateLimitResult.getCurrentUsage(),
                    rateLimitResult.getLimit(),
                    rateLimitResult.getRetryAfterSeconds(),
                    rateLimitResult.getThrottlingMessage()
            );
        }

        log.debug("Rate limit check PASSED for user {}. Proceeding to send Email. (Usage: {}/{})",
                client.getId(), rateLimitResult.getCurrentUsage(), rateLimitResult.getLimit());

        // get current algorithm
        String algorithm = getCurrentAlgorithm();

        NotificationLog log = new NotificationLog();
        log.setClientId(client.getId());
        log.setNotificationType(Constants.NOTIFICATION_EMAIL);
        log.setRecipient(request.getTo());
        log.setSubject(request.getSubject());
        log.setMessageContent(request.getBody());
        log.setAlgorithmUsed(algorithm);
        log.setTierAtSend(client.getTier().getName());
        log.setStatus(Constants.NOTIF_STATUS_SENT);
        log = notificationLogRepository.save(log);


        System.out.println("Email sent to: " + request.getTo() + " - Subject: " + request.getSubject());


        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setId(log.getId());
        notificationResponse.setStatus(Constants.NOTIF_STATUS_SENT);
        notificationResponse.setType(Constants.NOTIFICATION_EMAIL);
        notificationResponse.setRecipient(request.getTo());
        notificationResponse.setSentAt(log.getSentAt());
        notificationResponse.setMessage("Email sent successfully");

        return notificationResponse;
    }


    public List<NotificationResponse> sendBatch(User client, BatchNotificationRequest request) {

        List<NotificationResponse> responses = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;
        int rateLimitedCount = 0;

        for (BatchNotificationRequest.NotificationItem item : request.getNotifications()) {
            try {
                NotificationResponse response;

                if (Constants.NOTIFICATION_SMS.equals(item.getType())) {
                    SendSmsRequest smsRequest = new SendSmsRequest(item.getTo(), item.getMessage());
                    response = sendSms(client, smsRequest);
                    successCount++;
                } else if (Constants.NOTIFICATION_EMAIL.equals(item.getType())) {
                    SendEmailRequest emailRequest = new SendEmailRequest(
                            item.getTo(), item.getSubject(), item.getBody());
                    response = sendEmail(client, emailRequest);
                    successCount++;
                } else {
                    response = buildFailedResponse(item.getType(), item.getTo(), "Unknown notification type");
                    failedCount++;
                }

                responses.add(response);

            } catch (RateLimitExceededException e) {
                // Rate limit hit - log and continue with remaining
                log.warn("Batch item #{} rate limited for user {}: {}",
                        responses.size() + 1, client.getId(), e.getMessage());

                NotificationResponse failedResponse = buildFailedResponse(
                        item.getType(),
                        item.getTo(),
                        "Rate limit exceeded: " + e.getMessage()
                );


                responses.add(failedResponse);
                rateLimitedCount++;

                break;

            } catch (Exception e) {
                log.error("Batch item failed for user {}: {}", client.getId(), e.getMessage(), e);

                NotificationResponse failedResponse = buildFailedResponse(
                        item.getType(),
                        item.getTo(),
                        "Failed: " + e.getMessage()
                );
                responses.add(failedResponse);
                failedCount++;
            }
        }

        log.info("Batch processing complete for user {}. Success: {}, Failed: {}, Rate Limited: {}, Total: {}",
                client.getId(), successCount, failedCount, rateLimitedCount, responses.size());

        return responses;
    }


    private String getCurrentAlgorithm() {
        return systemConfigRepository.findByConfigKey("RATE_LIMIT_ALGORITHM")
                .map(SystemConfig::getConfigValue)
                .orElse(Constants.TOKEN_BUCKET);
    }


    private Map<String, Object> buildRateLimitInfo(RateLimitResult result) {
        Map<String, Object> info = new HashMap<>();
        info.put("limit", result.getLimit());
        info.put("remaining", result.getRemainingRequests());
        info.put("currentUsage", result.getCurrentUsage());
        info.put("throttlingLevel", result.getThrottlingLevel());
        info.put("limitType", result.getLimitType());
        info.put("algorithmUsed", result.getAlgorithmUsed());

        if ("SOFT".equals(result.getThrottlingLevel()) || "HARD".equals(result.getThrottlingLevel())) {
            info.put("throttlingMessage", result.getThrottlingMessage());
        }

        return info;
    }


    private NotificationResponse buildFailedResponse(String type, String recipient, String errorMessage) {
        NotificationResponse failedResponse = new NotificationResponse();
        failedResponse.setStatus(Constants.NOTIF_STATUS_FAILED);
        failedResponse.setType(type);
        failedResponse.setRecipient(recipient);
        failedResponse.setMessage(errorMessage);
        failedResponse.setSentAt(LocalDateTime.now());
        return failedResponse;
    }


    private void logFailedNotification(User client, String type, String recipient,
                                       String content, String subject, String reason) {
        NotificationLog log = new NotificationLog();
        log.setClientId(client.getId());
        log.setNotificationType(type);
        log.setRecipient(recipient);
        log.setMessageContent(content);
        log.setSubject(subject);
        log.setAlgorithmUsed(getCurrentAlgorithm());
        log.setTierAtSend(client.getTier().getName());
        log.setStatus(Constants.NOTIF_STATUS_FAILED);
        notificationLogRepository.save(log);
    }
}