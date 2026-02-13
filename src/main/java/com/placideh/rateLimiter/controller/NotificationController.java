package com.placideh.rateLimiter.controller;


import com.placideh.rateLimiter.dto.request.BatchNotificationRequest;
import com.placideh.rateLimiter.dto.request.SendEmailRequest;
import com.placideh.rateLimiter.dto.request.SendSmsRequest;
import com.placideh.rateLimiter.dto.response.NotificationResponse;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
@Tag(name = "Notifications", description = "Send SMS and Email notifications (Rate Limited)")
@SecurityRequirement(name = "API Key Authentication")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @Operation(summary = "Send SMS", description = "Send an SMS notification. Rate limited based on client tier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SMS sent successfully",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing API key"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/sms")
    public ResponseEntity<NotificationResponse> sendSms(
            @Valid @RequestBody SendSmsRequest request,
            HttpServletRequest httpRequest) {

        // get client from request attribute set from RateLimitInterceptor
        User client = (User) httpRequest.getAttribute("client");

        NotificationResponse response = notificationService.sendSms(client, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Send Email", description = "Send an email notification. Rate limited based on client tier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing API key"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(
            @Valid @RequestBody SendEmailRequest request,
            HttpServletRequest httpRequest) {

        // get client from request attribute set from RateLimitInterceptor
        User client = (User) httpRequest.getAttribute("client");

        NotificationResponse response = notificationService.sendEmail(client, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Send Batch Notifications", description = "Send multiple SMS/Email notifications in one request. Each notification counts against rate limit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch processed (may contain partial failures)"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing API key"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/batch")
    public ResponseEntity<List<NotificationResponse>> sendBatch(
            @Valid @RequestBody BatchNotificationRequest request,
            HttpServletRequest httpRequest) {

        // get client from request attribute set from RateLimitInterceptor
        User client = (User) httpRequest.getAttribute("client");

        List<NotificationResponse> responses = notificationService.sendBatch(client, request);
        return ResponseEntity.ok(responses);
    }
}
