package com.schoolsystem.repository;

import com.schoolsystem.entity.User;
import com.schoolsystem.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Data access layer for User entity
 * 
 * Spring Data JPA automatically implements these methods!
 * We just declare the method signature, Spring generates the SQL
 * 
 * Why JpaRepository?
 * - Provides CRUD operations out of the box
 * - Automatic query generation from method names
 * - Pagination and sorting support
 * - No boilerplate code needed
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * 
     * Spring Data JPA automatically generates:
     * SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     * 
     * Generated SQL:
     * SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if username exists
     * 
     * Generated SQL:
     * SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    Boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    Boolean existsByEmail(String email);
    
    /**
     * Find all users by role
     * 
     * Generated SQL:
     * SELECT * FROM users WHERE role = ?
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find all active users
     * 
     * Generated SQL:
     * SELECT * FROM users WHERE is_active = true
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find user by username and active status
     * 
     * Combining multiple conditions in method name
     */
    Optional<User> findByUsernameAndIsActiveTrue(String username);
}
