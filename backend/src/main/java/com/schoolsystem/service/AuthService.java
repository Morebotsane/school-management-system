package com.schoolsystem.service;

import com.schoolsystem.dto.LoginRequest;
import com.schoolsystem.dto.LoginResponse;
import com.schoolsystem.entity.User;
import com.schoolsystem.repository.UserRepository;
import com.schoolsystem.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * AuthService - Business logic for authentication
 * 
 * Handles:
 * - User login (with optional 2FA)
 * - Token generation
 * - Password validation
 * - Login tracking (last login timestamp)
 */
@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TwoFactorAuthService twoFactorAuthService;
    
    /**
     * Authenticate user and generate JWT token
     * 
     * Process:
     * 1. Verify credentials (username + password)
     * 2. Check if 2FA is enabled
     * 3. If 2FA enabled: send code and return "requires 2FA" response
     * 4. If no 2FA: generate token and return login response
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        // 2. Load user from database
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 3. Check if 2FA is enabled
        if (user.getTwoFactorEnabled()) {
            // Send 2FA code via SMS (Africa's Talking)
            twoFactorAuthService.sendTwoFactorCode(user);
            
            // Return response indicating 2FA is required
            return new LoginResponse(true);
        }
        
        // 4. Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 5. Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);
        
        // 6. Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // 7. Return successful login response
        return new LoginResponse(
            jwt,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().toString()
        );
    }
    
    /**
     * Verify 2FA code and complete login
     * 
     * Called after user enters the SMS code
     */
    @Transactional
    public LoginResponse verifyTwoFactorAndLogin(String username, String code) {
        // 1. Verify the 2FA code
        boolean isValid = twoFactorAuthService.verifyTwoFactorCode(username, code);
        
        if (!isValid) {
            throw new RuntimeException("Invalid verification code");
        }
        
        // 2. Load user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 3. Generate JWT token
        String jwt = tokenProvider.generateTokenFromUsername(
            user.getUsername(),
            user.getId(),
            user.getRole().toString()
        );
        
        // 4. Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // 5. Return login response
        return new LoginResponse(
            jwt,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().toString()
        );
    }
    
    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Encode and set new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Enable/disable 2FA for user
     */
    @Transactional
    public void toggleTwoFactorAuth(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setTwoFactorEnabled(enable);
        
        if (enable) {
            // Generate and store 2FA secret
            String secret = twoFactorAuthService.generateTwoFactorSecret();
            user.setTwoFactorSecret(secret);
        } else {
            user.setTwoFactorSecret(null);
        }
        
        userRepository.save(user);
    }
}
