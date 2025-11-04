package com.schoolsystem.security;

import com.schoolsystem.entity.User;
import com.schoolsystem.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * UserPrincipal - Represents the authenticated user in Spring Security
 * 
 * Implements UserDetails interface required by Spring Security
 * This is what gets stored in the SecurityContext after successful authentication
 */
public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private UserRole role;
    private boolean isActive;
    private Collection<? extends GrantedAuthority> authorities;
    
    public UserPrincipal(Long id, String username, String email, String password, 
                        UserRole role, boolean isActive,
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.authorities = authorities;
    }
    
    /**
     * Factory method to create UserPrincipal from User entity
     * 
     * This converts our database User into Spring Security's UserDetails
     */
    public static UserPrincipal create(User user) {
        // Convert role to Spring Security authority
        // ROLE_ prefix is Spring Security convention
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getRole(),
            user.getIsActive(),
            Collections.singletonList(authority)
        );
    }
    
    // Getters for our custom fields
    public Long getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    // UserDetails interface methods (required by Spring Security)
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
