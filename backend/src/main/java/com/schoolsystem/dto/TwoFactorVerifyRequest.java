package com.schoolsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * TwoFactorVerifyRequest DTO
 * 
 * Used when user enters the 6-digit code sent via SMS
 */
public class TwoFactorVerifyRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Code must be 6 digits")
    private String code;
    
    // Constructors
    public TwoFactorVerifyRequest() {
    }
    
    public TwoFactorVerifyRequest(String username, String code) {
        this.username = username;
        this.code = code;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}
