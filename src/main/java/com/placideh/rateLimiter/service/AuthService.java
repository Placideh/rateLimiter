package com.placideh.rateLimiter.service;


import com.placideh.rateLimiter.dto.request.CreateUserRequest;
import com.placideh.rateLimiter.dto.request.LoginRequest;
import com.placideh.rateLimiter.dto.response.AuthResponse;
import com.placideh.rateLimiter.dto.response.UserResponse;
import com.placideh.rateLimiter.exception.ResourceNotFoundException;
import com.placideh.rateLimiter.model.Tier;
import com.placideh.rateLimiter.model.User;
import com.placideh.rateLimiter.repository.TierRepository;
import com.placideh.rateLimiter.repository.UserRepository;
import com.placideh.rateLimiter.security.JwtTokenProvider;
import com.placideh.rateLimiter.util.ApiKeyGenerator;
import com.placideh.rateLimiter.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TierRepository tierRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApiKeyGenerator apiKeyGenerator;


    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new AuthResponse(jwt, user.getId(), user.getUsername(),
                user.getEmail(), user.getRole(), user.getId(),user.getApiKey());
    }

    @Transactional
    public UserResponse register(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) throw new IllegalArgumentException("Username already exists");

        if (userRepository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("Email already exists");



        // Verify tier exists
        Tier freeTier = tierRepository.findByName("FREE")
                .orElseThrow(() -> new ResourceNotFoundException("FREE tier not found. Please create default tiers first."));

        String plainApiKey = apiKeyGenerator.generateApiKey();
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Constants.ROLE_COMPANY);
        user.setTierId(freeTier.getId());
        user.setTier(freeTier);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        user.setApiKey(plainApiKey);

        user = userRepository.save(user);


        return mapToResponse(user);
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
