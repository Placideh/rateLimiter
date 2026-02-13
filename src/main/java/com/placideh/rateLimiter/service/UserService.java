package com.placideh.rateLimiter.service;




import com.placideh.rateLimiter.dto.request.UpdateUserRequest;
import com.placideh.rateLimiter.dto.request.UpdateUserTierRequest;
import com.placideh.rateLimiter.dto.response.UserResponse;
import com.placideh.rateLimiter.exception.ResourceNotFoundException;
import com.placideh.rateLimiter.model.Tier;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.repository.TierRepository;
import com.placideh.rateLimiter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TierRepository tierRepository;



    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    public User findApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", apiKey));
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        // Update email if provided
        if (request.getEmail() != null) {
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        user = userRepository.save(user);
        return mapToResponse(user);
    }


    @Transactional
    public UserResponse updateUserTier(String userId, UpdateUserTierRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // check new tier exists and is active
        Tier newTier = tierRepository.findById(request.getTierId())
                .orElseThrow(() -> new ResourceNotFoundException("Tier", "id", request.getTierId()));

        if (!newTier.getIsActive()) {
            throw new IllegalArgumentException("Cannot assign user to inactive tier");
        }

        // get old tier name for logging
        String oldTierName = user.getTier() != null ? user.getTier().getName() : "UNKNOWN";
        String newTierName = newTier.getName();

        // check if it is an upgrade or downgrade
        boolean isUpgrade = isUpgrade(oldTierName, newTierName);

        logger.info("Tier change for user {}: {} -> {} ({})",
                user.getUsername(), oldTierName, newTierName,
                isUpgrade ? "UPGRADE" : "DOWNGRADE");


        // Update tier
        user.setTierId(newTier.getId());
        user.setTier(newTier);
        user = userRepository.save(user);

        return mapToResponse(user);
    }




    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }




    private boolean isUpgrade(String oldTier, String newTier) {

        String[] tierOrder = {"FREE", "BASIC", "PROFESSIONAL", "ENTERPRISE"};

        int oldIndex = indexOfTier(tierOrder, oldTier);
        int newIndex = indexOfTier(tierOrder, newTier);

        return newIndex > oldIndex;
    }

    private int indexOfTier(String[] tiers, String tierName) {
        for (int i = 0; i < tiers.length; i++) {
            if (tiers[i].equalsIgnoreCase(tierName)) {
                return i;
            }
        }
        return -1; // not existing tier
    }

    private UserResponse mapToResponse(User user) {

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getTierId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setTierId(user.getTierId());
        userResponse.setTierName(user.getTier() != null ? user.getTier().getName() : null);
        userResponse.setIsActive(user.getIsActive());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}