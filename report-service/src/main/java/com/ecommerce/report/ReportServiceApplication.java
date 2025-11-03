package com.ecommerce.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Report Service Application.
 * 
 * <p>Implements CQRS pattern for read-optimized reporting.
 * Consumes events from Kafka to build read models.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.ecommerce.report",
    "com.ecommerce.common"
})
@EnableCaching
@EnableJpaAuditing
@EnableAsync
public class ReportServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}





