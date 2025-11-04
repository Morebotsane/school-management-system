package com.schoolsystem.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest DTO - Data Transfer Object for login endpoint
 * 
 * Why DTOs?
 * 1. Decouple API from database entities
 * 2. Control what data comes in/goes out
 * 3. Add validation rules
 * 4. API versioning flexibility
 * 
 * This is what the frontend sends to login
 */
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Constructors
    public LoginRequest() {
    }
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
