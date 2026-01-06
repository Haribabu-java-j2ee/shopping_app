package com.ecommerce.order.dto;

import com.ecommerce.order.domain.entity.VolumeCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for customer volume category information.
 * 
 * <p>This DTO represents a customer along with their volume category classification
 * based on total (aggregate) transaction value. The category is determined by
 * summing all transaction amounts for the customer.
 * 
 * <p><b>Classification Logic:</b>
 * <ul>
 *   <li>HIGH_VOLUME: Total order value &gt; 50,000</li>
 *   <li>MID_VOLUME: Total order value between 20,000 and 50,000</li>
 *   <li>LOW_VOLUME: Total order value &lt; 20,000</li>
 * </ul>
 * 
 * <p><b>Java 8 Streams Learning:</b> This DTO is populated using stream operations
 * such as {@code groupingBy}, {@code reducing}, and {@code Collectors.toMap} to 
 * aggregate and categorize customers based on their total spending volume.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see VolumeCategory
 * @see com.ecommerce.order.service.CustomerStatsService#getCustomerVolumeCategories()
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerVolumeCategoryDTO {
    
    /**
     * Unique identifier of the customer.
     */
    private Long customerId;
    
    /**
     * The volume category classification for this customer.
     * Determined by aggregating all transaction amounts.
     */
    private VolumeCategory volumeCategory;
    
    /**
     * Human-readable description of the volume category.
     */
    private String categoryDescription;
    
    /**
     * Total cumulative transaction value for this customer.
     * Sum of all order amounts.
     */
    private BigDecimal totalOrderValue;
    
    /**
     * Total number of orders placed by this customer.
     */
    private Integer totalOrders;
}

