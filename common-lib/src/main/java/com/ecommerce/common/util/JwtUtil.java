package com.ecommerce.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * 
 * <p>Handles JWT token generation, validation, and claims extraction.
 * Implements Singleton Pattern through Spring's @Component annotation.
 * 
 * <p><b>Security Features:</b></p>
 * <ul>
 *   <li>HMAC-SHA256 signature algorithm</li>
 *   <li>Configurable token expiration</li>
 *   <li>Claims-based authorization</li>
 *   <li>Token validation and verification</li>
 * </ul>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Component
public class JwtUtil {
    
    /**
     * Secret key for signing JWT tokens.
     * Should be at least 256 bits for HS256 algorithm.
     */
    @Value("${jwt.secret:ecommerce-shopping-app-secret-key-change-in-production-minimum-256-bits}")
    private String secret;
    
    /**
     * Token expiration time in milliseconds.
     * Default: 24 hours (86400000 ms)
     */
    @Value("${jwt.expiration:86400000}")
    private Long expiration;
    
    /**
     * Refresh token expiration time in milliseconds.
     * Default: 7 days (604800000 ms)
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;
    
    /**
     * Generates a secret key from the configured secret string.
     * 
     * @return SecretKey for JWT signing
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extracts the username (subject) from the JWT token.
     * 
     * @param token JWT token
     * @return Username extracted from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts the expiration date from the JWT token.
     * 
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts a specific claim from the JWT token.
     * 
     * <p>This method demonstrates the Strategy Pattern by accepting
     * a function that defines how to extract the claim.
     * 
     * @param <T> Type of the claim
     * @param token JWT token
     * @param claimsResolver Function to extract the claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from the JWT token.
     * 
     * @param token JWT token
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Checks if the JWT token has expired.
     * 
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Generates a JWT token for a username.
     * 
     * @param username Username to include in token
     * @return Generated JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    /**
     * Generates a JWT token with custom claims.
     * 
     * @param username Username to include in token
     * @param additionalClaims Additional claims to include
     * @return Generated JWT token
     */
    public String generateToken(String username, Map<String, Object> additionalClaims) {
        return createToken(additionalClaims, username);
    }
    
    /**
     * Generates a JWT token with roles.
     * 
     * @param username Username to include in token
     * @param roles List of roles for the user
     * @return Generated JWT token
     */
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, username);
    }
    
    /**
     * Generates a refresh token.
     * 
     * @param username Username to include in token
     * @return Generated refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extracts roles from the JWT token.
     * 
     * @param token JWT token
     * @return List of roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        final Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }
        return new ArrayList<>();
    }
    
    /**
     * Gets the token expiration time in seconds.
     * 
     * @return Expiration time in seconds
     */
    public Long getExpirationTime() {
        return expiration / 1000; // Convert milliseconds to seconds
    }
    
    /**
     * Creates a JWT token with specified claims and subject.
     * 
     * <p><b>Token Structure:</b></p>
     * <ul>
     *   <li>Claims: Custom data</li>
     *   <li>Subject: Username</li>
     *   <li>Issued At: Current timestamp</li>
     *   <li>Expiration: Configured expiration time</li>
     *   <li>Signature: HMAC-SHA256</li>
     * </ul>
     * 
     * @param claims Custom claims to include
     * @param subject Token subject (username)
     * @return Created JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Validates a JWT token against a username.
     * 
     * <p>Checks if:
     * <ul>
     *   <li>Token username matches the provided username</li>
     *   <li>Token has not expired</li>
     * </ul>
     * 
     * @param token JWT token to validate
     * @param username Username to validate against
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Error validating JWT token", e);
            return false;
        }
    }
    
    /**
     * Validates a JWT token without username verification.
     * 
     * @param token JWT token to validate
     * @return true if token is valid and not expired, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validating JWT token", e);
            return false;
        }
    }
}


