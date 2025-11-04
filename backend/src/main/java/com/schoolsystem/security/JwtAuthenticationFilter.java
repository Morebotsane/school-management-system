package com.schoolsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * This filter runs BEFORE every request to:
 * 1. Extract JWT token from Authorization header
 * 2. Validate the token
 * 3. Load user details
 * 4. Set authentication in SecurityContext
 * 
 * Flow:
 * Request → Filter → Validate Token → Load User → Set Auth → Continue to Controller
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
                                   throws ServletException, IOException {
        try {
            // 1. Get JWT token from request header
            String jwt = getJwtFromRequest(request);
            
            // 2. Validate token and extract username
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                // 3. Load user details from database
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                
                // 4. Create authentication object
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 5. Set authentication in SecurityContext
                // Now Spring Security knows who the user is!
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        // Continue with the request
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     * 
     * Expected format: "Bearer <token>"
     * Example: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWI..."
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Remove "Bearer " prefix and return the token
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
