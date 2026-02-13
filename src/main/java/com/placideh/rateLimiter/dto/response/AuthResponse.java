package com.placideh.rateLimiter.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String userId;
    private String username;
    private String email;
    private String role;
    private String apiKey;

    public AuthResponse(String token, String userId, String username, String email, String role, String apiKey) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.apiKey = apiKey;
    }


    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getApiKey() {
        return apiKey;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}