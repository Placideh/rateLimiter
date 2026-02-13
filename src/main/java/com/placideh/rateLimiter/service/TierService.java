package com.placideh.rateLimiter.service;

import com.placideh.rateLimiter.dto.request.CreateTierRequest;
import com.placideh.rateLimiter.dto.response.TierResponse;
import com.placideh.rateLimiter.exception.ResourceNotFoundException;
import com.placideh.rateLimiter.model.Tier;
import com.placideh.rateLimiter.repository.TierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TierService {

    @Autowired
    private TierRepository tierRepository;

    public TierResponse createTier(CreateTierRequest request) {
        if (tierRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tier with this name already exists");
        }

        Tier tier = new Tier();
        tier.setName(request.getName());
        tier.setDescription(request.getDescription());
        tier.setRequestsPerMinute(request.getRequestsPerMinute());
        tier.setRequestsPerMonth(request.getRequestsPerMonth());
        tier.setPricePerMonth(request.getPricePerMonth());
        tier.setCreatedAt(LocalDateTime.now());
        tier.setIsActive(true);

        tier = tierRepository.save(tier);
        return mapToResponse(tier);
    }

    public List<TierResponse> getAllTiers() {
        return tierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TierResponse getTierById(String id) {
        Tier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier", "id", id));
        return mapToResponse(tier);
    }

    public TierResponse updateTier(String id, CreateTierRequest request) {
        Tier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier", "id", id));

        if (request.getName() != null) {
            tier.setName(request.getName());
        }
        if (request.getDescription() != null) {
            tier.setDescription(request.getDescription());
        }
        if (request.getRequestsPerMinute() != null) {
            tier.setRequestsPerMinute(request.getRequestsPerMinute());
        }
        if (request.getRequestsPerMonth() != null) {
            tier.setRequestsPerMonth(request.getRequestsPerMonth());
        }
        if (request.getPricePerMonth() != null) {
            tier.setPricePerMonth(request.getPricePerMonth());
        }

        tier = tierRepository.save(tier);
        return mapToResponse(tier);
    }

    public void deleteTier(String id) {
        if (!tierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tier", "id", id);
        }
        tierRepository.deleteById(id);
    }

    private TierResponse mapToResponse(Tier tier) {
        TierResponse tierResponse = new TierResponse();
        tierResponse.setId(tier.getId());
        tierResponse.setName(tier.getName());
        tierResponse.setDescription(tier.getDescription());
        tierResponse.setRequestsPerMinute(tier.getRequestsPerMinute());
        tierResponse.setPricePerMonth(tier.getPricePerMonth());
        tierResponse.setIsActive(tier.getIsActive());

        return tierResponse;
    }
}
