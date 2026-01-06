package com.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Order Service.
 * 
 * <p>This microservice handles:
 * <ul>
 *   <li>Order creation and management</li>
 *   <li>Order status tracking</li>
 *   <li>Inventory validation</li>
 *   <li>Payment processing coordination</li>
 *   <li>Event publishing to Kafka</li>
 * </ul>
 * 
 * <p><b>Design Patterns Implemented:</b></p>
 * <ul>
 *   <li>Repository Pattern: Data access abstraction</li>
 *   <li>Saga Pattern: Distributed transaction management</li>
 *   <li>Event Sourcing: Order state tracking through events</li>
 *   <li>Circuit Breaker: Resilience for external service calls</li>
 *   <li>Factory Pattern: Order creation strategies</li>
 *   <li>State Pattern: Order state transitions</li>
 * </ul>
 * 
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>@EnableCaching: Redis-based caching</li>
 *   <li>@EnableJpaAuditing: Automatic entity auditing</li>
 *   <li>@EnableAsync: Asynchronous event processing</li>
 *   <li>@EnableScheduling: Scheduled tasks (e.g., order timeout)</li>
 * </ul>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.ecommerce.order",
    "com.ecommerce.common"
})
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class OrderServiceApplication {
    
    /**
     * Main entry point for the Order Service application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}








