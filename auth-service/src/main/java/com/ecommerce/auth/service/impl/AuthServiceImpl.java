package com.ecommerce.auth.service.impl;

import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.RoleType;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.dto.*;
import com.ecommerce.auth.mapper.UserMapper;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.UnauthorizedException;
import com.ecommerce.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Implementation of AuthService for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final Long REFRESH_TOKEN_EXPIRY = 604800L; // 7 days in seconds
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username is already taken");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already registered");
        }
        
        // Create new user
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Assign default role
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default user role not found"));
        user.addRole(userRole);
        
        // Save user
        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getUsername(), 
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()));
        
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        
        // Store refresh token in Redis
        storeRefreshToken(user.getUsername(), refreshToken);
        
        UserDTO userDTO = userMapper.toDTO(user);
        
        return AuthResponse.createBearerToken(accessToken, refreshToken, jwtUtil.getExpirationTime(), userDTO);
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsernameOrEmail());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            
            // Get user details
            User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
            
            // Check if account is locked
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new UnauthorizedException("Account is locked. Please try again later.");
            }
            
            // Reset failed login attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate tokens
            String accessToken = jwtUtil.generateToken(user.getUsername(),
                    user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toList()));
            
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            // Store refresh token in Redis
            storeRefreshToken(user.getUsername(), refreshToken);
            
            UserDTO userDTO = userMapper.toDTO(user);
            
            log.info("User logged in successfully: {}", user.getUsername());
            return AuthResponse.createBearerToken(accessToken, refreshToken, jwtUtil.getExpirationTime(), userDTO);
            
        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            handleFailedLogin(request.getUsernameOrEmail());
            throw new UnauthorizedException("Invalid credentials");
        }
    }
    
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        String username = jwtUtil.extractUsername(refreshToken);
        
        // Check if refresh token exists in Redis
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new UnauthorizedException("Refresh token not found or expired");
        }
        
        // Get user details
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getUsername(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()));
        
        UserDTO userDTO = userMapper.toDTO(user);
        
        return AuthResponse.createBearerToken(newAccessToken, refreshToken, jwtUtil.getExpirationTime(), userDTO);
    }
    
    @Override
    public void logout(String token) {
        String username = jwtUtil.extractUsername(token);
        
        // Add token to blacklist
        Long expirationTime = jwtUtil.getExpirationTime();
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "true",
                expirationTime,
                TimeUnit.SECONDS
        );
        
        // Remove refresh token
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + username);
        
        log.info("User logged out successfully: {}", username);
    }
    
    @Override
    public Boolean validateToken(String token) {
        // Check if token is blacklisted
        Boolean isBlacklisted = redisTemplate.hasKey(BLACKLIST_PREFIX + token);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            return false;
        }
        
        return jwtUtil.validateToken(token);
    }
    
    @Override
    public UserDTO getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return userMapper.toDTO(user);
    }
    
    /**
     * Store refresh token in Redis.
     * 
     * @param username The username
     * @param refreshToken The refresh token
     */
    private void storeRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + username,
                refreshToken,
                REFRESH_TOKEN_EXPIRY,
                TimeUnit.SECONDS
        );
    }
    
    /**
     * Handle failed login attempt by incrementing counter and locking account if needed.
     * 
     * @param usernameOrEmail The username or email
     */
    private void handleFailedLogin(String usernameOrEmail) {
        userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .ifPresent(user -> {
                    int attempts = user.getFailedLoginAttempts() + 1;
                    user.setFailedLoginAttempts(attempts);
                    
                    // Lock account after 5 failed attempts
                    if (attempts >= 5) {
                        user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                        log.warn("Account locked due to multiple failed login attempts: {}", user.getUsername());
                    }
                    
                    userRepository.save(user);
                });
    }
}



