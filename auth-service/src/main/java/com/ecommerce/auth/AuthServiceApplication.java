package com.ecommerce.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the Authentication Service.
 * 
 * <p>This microservice handles:
 * <ul>
 *   <li>User registration and authentication</li>
 *   <li>JWT token generation and validation</li>
 *   <li>Role-based access control (RBAC)</li>
 *   <li>Session management with Redis</li>
 *   <li>Password encryption and security</li>
 * </ul>
 * 
 * <p><b>Design Patterns Implemented:</b></p>
 * <ul>
 *   <li>Strategy Pattern: Multiple authentication strategies</li>
 *   <li>Factory Pattern: Token and user creation</li>
 *   <li>Singleton Pattern: Security configuration</li>
 *   <li>Decorator Pattern: Enhanced authentication with 2FA</li>
 * </ul>
 * 
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>@EnableCaching: Redis-based token caching</li>
 *   <li>@EnableJpaAuditing: Automatic entity auditing</li>
 *   <li>@EnableAsync: Asynchronous email notifications</li>
 * </ul>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.ecommerce.auth",
    "com.ecommerce.common"
})
@EnableCaching
@EnableJpaAuditing
@EnableAsync
public class AuthServiceApplication {
    
    /**
     * Main entry point for the Authentication Service application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}



