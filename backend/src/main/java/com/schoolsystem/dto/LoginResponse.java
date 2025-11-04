package com.schoolsystem.dto;

/**
 * LoginResponse DTO - Response from login endpoint
 * 
 * This is what we send back to the frontend after successful login
 * Contains everything the frontend needs to maintain the session
 */
public class LoginResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
    private boolean requiresTwoFactor;
    
    // Constructor for successful login without 2FA
    public LoginResponse(String accessToken, Long userId, String username, 
                        String email, String role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.requiresTwoFactor = false;
    }
    
    // Constructor for 2FA required
    public LoginResponse(boolean requiresTwoFactor) {
        this.requiresTwoFactor = requiresTwoFactor;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isRequiresTwoFactor() {
        return requiresTwoFactor;
    }
    
    public void setRequiresTwoFactor(boolean requiresTwoFactor) {
        this.requiresTwoFactor = requiresTwoFactor;
    }
}
