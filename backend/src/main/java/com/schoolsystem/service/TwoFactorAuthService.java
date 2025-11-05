package com.schoolsystem.service;

import com.schoolsystem.entity.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
//import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TwoFactorAuthService - Handles 2FA code generation and verification
 * 
 * For now: In-memory storage (for development/testing)
 * Production: Use Redis or database table with expiration
 * 
 * Africa's Talking integration will be added here for SMS sending
 */
@Service
public class TwoFactorAuthService {
    
    // In-memory storage of verification codes
    // Key: username, Value: code
    // TODO: Replace with Redis in production for distributed systems
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    
    // Code expiration tracker
    private final Map<String, Long> codeExpirations = new ConcurrentHashMap<>();
    
    // Code valid for 5 minutes (in milliseconds)
    private static final long CODE_EXPIRATION_TIME = 5 * 60 * 1000;
    
    /**
     * Generate and send 2FA code via SMS
     * 
     * Process:
     * 1. Generate random 6-digit code
     * 2. Store code with expiration
     * 3. Send via Africa's Talking SMS API
     */
    public void sendTwoFactorCode(User user) {
        // 1. Generate 6-digit code
        String code = generateSixDigitCode();
        
        // 2. Store code and expiration
        verificationCodes.put(user.getUsername(), code);
        codeExpirations.put(user.getUsername(), System.currentTimeMillis() + CODE_EXPIRATION_TIME);
        
        // 3. Send SMS via Africa's Talking
        sendSmsCode(user, code);
        
        System.out.println("2FA Code for " + user.getUsername() + ": " + code);
    }
    
    /**
     * Verify 2FA code entered by user
     */
    public boolean verifyTwoFactorCode(String username, String code) {
        // Check if code exists
        String storedCode = verificationCodes.get(username);
        if (storedCode == null) {
            return false;
        }
        
        // Check if code has expired
        Long expiration = codeExpirations.get(username);
        if (expiration == null || System.currentTimeMillis() > expiration) {
            // Code expired, remove it
            verificationCodes.remove(username);
            codeExpirations.remove(username);
            return false;
        }
        
        // Verify code matches
        boolean isValid = storedCode.equals(code);
        
        // If valid, remove the code (one-time use)
        if (isValid) {
            verificationCodes.remove(username);
            codeExpirations.remove(username);
        }
        
        return isValid;
    }
    
    /**
     * Generate random 6-digit code
     */
    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * Generate secret for 2FA (stored in user record)
     */
    public String generateTwoFactorSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return bytesToHex(bytes);
    }
    
    /**
     * Send SMS code via Africa's Talking
     * 
     * TODO: Implement actual Africa's Talking integration
     * For now, just logs the code
     */
    private void sendSmsCode(User user, String code) {
        // Get phone number from user profile
        String phoneNumber = getUserPhoneNumber(user);
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new RuntimeException("User does not have a phone number for 2FA");
        }
        
        String message = String.format(
            "Your School Management System verification code is: %s. Valid for 5 minutes. Do not share this code.",
            code
        );
        
        // TODO: Integrate Africa's Talking SMS API
        // AfricasTalkingService.sendSms(phoneNumber, message);
        
        // For development: just log it
        System.out.println("SMS to " + phoneNumber + ": " + message);
    }
    
    /**
     * Helper to get phone number from user
     */
    private String getUserPhoneNumber(User user) {
        // TODO: Get from user profile when we implement UserProfile entity
        // For now, return null (will be implemented later)
        return null;
    }
    
    /**
     * Utility: Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
