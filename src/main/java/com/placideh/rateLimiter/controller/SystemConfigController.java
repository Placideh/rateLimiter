package com.placideh.rateLimiter.controller;


import com.placideh.rateLimiter.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.util.Map;

@RestController
@RequestMapping("/admin/config")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "System Configuration (Admin)", description = "Manage system settings and rate limiting algorithms")
@SecurityRequirement(name = "Bearer Authentication")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Operation(summary = "Get All Configurations",
            description = "Retrieve all system configuration settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved configurations",
                    content = @Content(
                            schema = @Schema(example = "{\"RATE_LIMIT_ALGORITHM\": \"TOKEN_BUCKET\"}")
                    )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required")
    })
    @GetMapping
    public ResponseEntity<Map<String, String>> getAllConfigs() {
        Map<String, String> configs = systemConfigService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    @Operation(summary = "Get Current Rate Limiting Algorithm",
            description = "Retrieve the currently active rate limiting algorithm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved algorithm",
                    content = @Content(
                            schema = @Schema(example = "{\"algorithm\": \"TOKEN_BUCKET\", \"description\": \"Token Bucket - Smooth rate limiting with token refill\"}")
                    )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required")
    })
    @GetMapping("/algorithm")
    public ResponseEntity<Map<String, String>> getCurrentAlgorithm() {
        String algorithm = systemConfigService.getCurrentAlgorithm();
        return ResponseEntity.ok(Map.of(
                "algorithm", algorithm,
                "description", algorithm.equals("TOKEN_BUCKET")
                        ? "Token Bucket - Smooth rate limiting with token refill"
                        : "Fixed Window - Simple window-based rate limiting"
        ));
    }

    @Operation(summary = "Update Rate Limiting Algorithm",
            description = "Switch between rate limiting algorithms. Changes take effect immediately for new requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algorithm updated successfully",
                    content = @Content(
                            schema = @Schema(example = "{\"message\": \"Algorithm updated successfully\", \"algorithm\": \"FIXED_WINDOW\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid algorithm value. Must be TOKEN_BUCKET or FIXED_WINDOW"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Algorithm to set",
            required = true,
            content = @Content(
                    schema = @Schema(example = "{\"algorithm\": \"TOKEN_BUCKET\"}"),
                    examples = {
                            @ExampleObject(
                                    name = "Token Bucket",
                                    value = "{\"algorithm\": \"TOKEN_BUCKET\"}",
                                    description = "Smooth rate limiting with token refill"
                            ),
                            @ExampleObject(
                                    name = "Fixed Window",
                                    value = "{\"algorithm\": \"FIXED_WINDOW\"}",
                                    description = "Simple window-based rate limiting"
                            )
                    }
            )
    )
    @PutMapping("/algorithm")
    public ResponseEntity<Map<String, String>> updateAlgorithm(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String algorithm = request.get("algorithm");
        systemConfigService.updateConfig("RATE_LIMIT_ALGORITHM", algorithm, authentication.getName());

        return ResponseEntity.ok(Map.of(
                "message", "Algorithm updated successfully",
                "algorithm", algorithm
        ));
    }
}
