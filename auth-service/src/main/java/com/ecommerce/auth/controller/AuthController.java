package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.*;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * 
 * <p>Provides endpoints for:
 * <ul>
 *   <li>User registration</li>
 *   <li>User login</li>
 *   <li>Token refresh</li>
 *   <li>User logout</li>
 *   <li>Token validation</li>
 * </ul>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user.
     * 
     * @param request Registration request with user details
     * @return ApiResponse with authentication token and user details
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register - Registering new user: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    /**
     * Authenticate user and generate JWT token.
     * 
     * @param request Login request with credentials
     * @return ApiResponse with authentication token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login - User login attempt: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    /**
     * Refresh access token using refresh token.
     * 
     * @param request Refresh token request
     * @return ApiResponse with new authentication token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /auth/refresh - Refreshing access token");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
    
    /**
     * Logout user and invalidate tokens.
     * 
     * @param authorizationHeader Authorization header with Bearer token
     * @return ApiResponse confirming logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        log.info("POST /auth/logout - User logout");
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
    
    /**
     * Validate JWT token.
     * 
     * @param authorizationHeader Authorization header with Bearer token
     * @return ApiResponse with validation result
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String authorizationHeader) {
        log.info("GET /auth/validate - Validating token");
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token validation result", isValid));
    }
    
    /**
     * Get current user details from JWT token.
     * 
     * @param authorizationHeader Authorization header with Bearer token
     * @return ApiResponse with user details
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader) {
        log.info("GET /auth/me - Getting current user details");
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        UserDTO user = authService.getUserFromToken(token);
        return ResponseEntity.ok(ApiResponse.success("User details retrieved successfully", user));
    }
    
    /**
     * Health check endpoint.
     * 
     * @return Success response
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OK", "Auth service is healthy"));
    }
}

