package com.placideh.rateLimiter.controller;

import com.placideh.rateLimiter.dto.response.UsageResponse;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.repository.UserRepository;
import com.placideh.rateLimiter.service.UsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@Tag(name = "Usage & Analytics", description = "View usage statistics and subscription details")
@SecurityRequirement(name = "Bearer Authentication")
public class UsageController {

    @Autowired
    private UsageService usageService;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get Current Month Usage",
            description = "Retrieve current month usage statistics for authenticated client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved usage",
                    content = @Content(schema = @Schema(implementation = UsageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "404", description = "User or client not found")
    })
    @GetMapping("/client/usage/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<UsageResponse> getCurrentUsage(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UsageResponse response = usageService.getCurrentUsage(user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Usage History",
            description = "Retrieve historical usage data for past months")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved usage history",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsageResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @GetMapping("/client/usage/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<List<UsageResponse>> getUsageHistory(
            @RequestParam(defaultValue = "6") int months,
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UsageResponse> responses = usageService.getUsageHistory(user.getId(), months);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get Subscription Details",
            description = "Retrieve current subscription tier and limits")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription",
                    content = @Content(schema = @Schema(implementation = UsageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @GetMapping("/client/subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<UsageResponse> getSubscription(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UsageResponse response = usageService.getCurrentUsage(user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get All Clients Usage (Admin)",
            description = "Retrieve current month usage for all clients. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all usage",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsageResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required")
    })
    @GetMapping("/admin/usage/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsageResponse>> getAllClientsUsage() {
        List<UsageResponse> responses = usageService.getAllClientsUsage();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get Client Usage by ID (Admin)",
            description = "Retrieve usage statistics for a specific client. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved client usage",
                    content = @Content(schema = @Schema(implementation = UsageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required")
    })
    @GetMapping("/admin/usage/clients/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsageResponse> getClientUsage(@PathVariable String clientId) {
        UsageResponse response = usageService.getCurrentUsage(clientId);
        return ResponseEntity.ok(response);
    }
}
