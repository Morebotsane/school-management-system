package com.schoolsystem.security;

import com.schoolsystem.entity.User;
import com.schoolsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CustomUserDetailsService - Loads user from database for authentication
 * 
 * Spring Security calls this service when it needs to authenticate a user
 * This is the bridge between Spring Security and our database
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Load user by username
     * 
     * Called by Spring Security during authentication
     * Must return UserDetails or throw UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user in database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found with username: " + username)
                );
        
        // Check if user is active
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }
        
        // Convert to UserPrincipal (which implements UserDetails)
        return UserPrincipal.create(user);
    }
    
    /**
     * Load user by ID (used by JWT filter)
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found with id: " + id)
                );
        
        return UserPrincipal.create(user);
    }
}
