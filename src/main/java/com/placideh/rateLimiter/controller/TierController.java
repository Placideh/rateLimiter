package com.placideh.rateLimiter.controller;


import com.placideh.rateLimiter.dto.request.CreateTierRequest;
import com.placideh.rateLimiter.dto.response.TierResponse;
import com.placideh.rateLimiter.service.TierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tiers")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Tier Management (Admin)", description = "Manage subscription tiers and pricing plans")
@SecurityRequirement(name = "Bearer Authentication")
public class TierController {

    @Autowired
    private TierService tierService;


    @Operation(summary = "Create Tier", description = "Create a new subscription tier with rate limits and pricing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tier created successfully",
                    content = @Content(schema = @Schema(implementation = TierResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tier name already exists or invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required")
    })
    @PostMapping
    public ResponseEntity<TierResponse> createTier(@Valid @RequestBody CreateTierRequest request) {
        TierResponse response = tierService.createTier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Get All Tiers", description = "Retrieve list of all subscription tiers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tiers")
    @GetMapping
    public ResponseEntity<List<TierResponse>> getAllTiers() {
        List<TierResponse> tiers = tierService.getAllTiers();
        return ResponseEntity.ok(tiers);
    }

    @Operation(summary = "Get Tier by ID", description = "Retrieve a specific tier by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tier found",
                    content = @Content(schema = @Schema(implementation = TierResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tier not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TierResponse> getTierById(@PathVariable String id) {
        TierResponse tier = tierService.getTierById(id);
        return ResponseEntity.ok(tier);
    }

    @Operation(summary = "Update Tier", description = "Update tier limits and pricing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tier updated successfully",
                    content = @Content(schema = @Schema(implementation = TierResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tier not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TierResponse> updateTier(
            @PathVariable String id,
            @Valid @RequestBody CreateTierRequest request) {
        TierResponse response = tierService.updateTier(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete Tier", description = "Delete a subscription tier. WARNING: Cannot delete if clients are using this tier!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Tier not found"),
            @ApiResponse(responseCode = "400", description = "Tier is in use by clients")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTier(@PathVariable String id) {
        tierService.deleteTier(id);
        return ResponseEntity.noContent().build();
    }
}
