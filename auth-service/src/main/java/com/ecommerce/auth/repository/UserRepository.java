package com.ecommerce.auth.repository;

import com.ecommerce.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom queries for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find a user by username.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email address.
     * 
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by username or email.
     * 
     * @param username The username to search for
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * Check if a username already exists.
     * 
     * @param username The username to check
     * @return True if username exists, false otherwise
     */
    Boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists.
     * 
     * @param email The email to check
     * @return True if email exists, false otherwise
     */
    Boolean existsByEmail(String email);
    
    /**
     * Find a user by username with roles eagerly loaded.
     * 
     * @param username The username to search for
     * @return Optional containing the user with roles if found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(String username);
}



