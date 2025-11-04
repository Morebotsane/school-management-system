package com.schoolsystem.controller;

import com.schoolsystem.dto.LoginRequest;
import com.schoolsystem.dto.LoginResponse;
import com.schoolsystem.dto.TwoFactorVerifyRequest;
import com.schoolsystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - REST API endpoints for authentication
 * 
 * Endpoints:
 * POST /api/auth/login - Login with username/password
 * POST /api/auth/verify-2fa - Verify 2FA code
 * POST /api/auth/logout - Logout (optional, JWT is stateless)
 * 
 * All endpoints are public (no authentication required)
 * Configured in SecurityConfig
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)  // Allow CORS for auth endpoints
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * POST /api/auth/login
     * 
     * Login endpoint - accepts username and password
     * 
     * Request body:
     * {
     *   "username": "admin",
     *   "password": "Admin@123"
     * }
     * 
     * Response (no 2FA):
     * {
     *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "username": "admin",
     *   "email": "admin@school.com",
     *   "role": "ADMIN",
     *   "requiresTwoFactor": false
     * }
     * 
     * Response (2FA required):
     * {
     *   "requiresTwoFactor": true
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Login failed: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/auth/verify-2fa
     * 
     * Verify 2FA code and complete login
     * 
     * Request body:
     * {
     *   "username": "admin",
     *   "code": "123456"
     * }
     * 
     * Response (successful):
     * {
     *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "username": "admin",
     *   "email": "admin@school.com",
     *   "role": "ADMIN",
     *   "requiresTwoFactor": false
     * }
     */
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactor(@Valid @RequestBody TwoFactorVerifyRequest request) {
        try {
            LoginResponse response = authService.verifyTwoFactorAndLogin(
                request.getUsername(),
                request.getCode()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Verification failed: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/auth/logout
     * 
     * Logout endpoint (optional for JWT)
     * With JWT, logout is handled client-side by removing the token
     * 
     * This endpoint is mainly for logging purposes or if we add token blacklisting
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // With JWT, we don't need to do anything server-side
        // Client just deletes the token
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
    
    /**
     * GET /api/auth/test
     * 
     * Test endpoint to verify API is working
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new MessageResponse("Auth API is working!"));
    }
    
    // Inner classes for responses
    
    static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
