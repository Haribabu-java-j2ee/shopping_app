package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.*;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {
    
    /**
     * Register a new user.
     * 
     * @param request Registration request with user details
     * @return AuthResponse with JWT token and user information
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Authenticate a user and generate JWT token.
     * 
     * @param request Login request with credentials
     * @return AuthResponse with JWT token and user information
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Refresh access token using refresh token.
     * 
     * @param request Refresh token request
     * @return AuthResponse with new JWT token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);
    
    /**
     * Logout user and invalidate tokens.
     * 
     * @param token The JWT token to invalidate
     */
    void logout(String token);
    
    /**
     * Validate JWT token.
     * 
     * @param token The JWT token to validate
     * @return True if token is valid, false otherwise
     */
    Boolean validateToken(String token);
    
    /**
     * Get user details from JWT token.
     * 
     * @param token The JWT token
     * @return UserDTO with user information
     */
    UserDTO getUserFromToken(String token);
}






