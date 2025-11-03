package com.ecommerce.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing JWT token and user details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    private String accessToken;
    
    private String tokenType;
    
    private Long expiresIn;
    
    private String refreshToken;
    
    private UserDTO user;
    
    /**
     * Creates a standard Bearer token response.
     * 
     * @param accessToken The JWT access token
     * @param refreshToken The refresh token
     * @param expiresIn Token expiration time in seconds
     * @param user User details
     * @return AuthResponse with Bearer token type
     */
    public static AuthResponse createBearerToken(String accessToken, String refreshToken, Long expiresIn, UserDTO user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }
}

