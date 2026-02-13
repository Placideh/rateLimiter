package com.placideh.rateLimiter.interceptor;


import com.placideh.rateLimiter.exception.InvalidApiKeyException;
import com.placideh.rateLimiter.exception.RateLimitExceededException;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.service.RateLimiterService;
import com.placideh.rateLimiter.service.UserService;
import com.placideh.rateLimiter.service.rateLimit.RateLimitResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Rate Limit Interceptor
 * Intercepts notification endpoints and applies rate limiting
 *
 * Applied to: /notifications/**
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // get API key from header
        String apiKey = request.getHeader("X-API-Key");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new InvalidApiKeyException("API key is required. Please provide X-API-Key header.");
        }

        User client = userService.findApiKey(apiKey);

        if (client == null) {
            throw new InvalidApiKeyException("Invalid API key");
        }

        if (!client.getIsActive()) {
            throw new InvalidApiKeyException("Client account is inactive");
        }

        log.debug("Rate limiting check for client: {} ({})", client.getUsername(), client.getId());

        // handle all rate limits (system-wide → window → monthly)
        RateLimitResult result = rateLimiterService.checkAllLimits(client);

        if (!result.isAllowed()) {
            // Rate limit exceeded - log and throw exception
            log.warn("Rate limit exceeded for client {}: {} - {}",
                    client.getId(), result.getLimitType(), result.getThrottlingMessage());

            throw new RateLimitExceededException(
                    result.getLimitType(),
                    result.getCurrentUsage(),
                    result.getLimit(),
                    result.getRetryAfterSeconds(),
                    result.getThrottlingMessage()
            );
        }

        // add rate limit headers to response
        response.setHeader("X-RateLimit-Limit", String.valueOf(result.getLimit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemainingRequests()));
        response.setHeader("X-RateLimit-Type", result.getLimitType());
        response.setHeader("X-RateLimit-Throttling-Level", result.getThrottlingLevel());
        response.setHeader("X-RateLimit-Algorithm", result.getAlgorithmUsed());

        // add warning header for soft throttling
        if ("SOFT".equals(result.getThrottlingLevel())) {
            response.setHeader("X-RateLimit-Warning", result.getThrottlingMessage());
            log.info("Soft throttle warning for client {}: {}", client.getId(), result.getThrottlingMessage());
        }

        // store client in request attribute for controllers
        request.setAttribute("client", client);
        request.setAttribute("rateLimitResult", result);

        log.debug("Rate limit check passed for client {}: {}/{} requests",
                client.getId(), result.getCurrentUsage(), result.getLimit());

        return true;
    }
}

