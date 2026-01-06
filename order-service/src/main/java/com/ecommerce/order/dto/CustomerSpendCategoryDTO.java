package com.ecommerce.order.dto;

import com.ecommerce.order.domain.entity.SpendCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for customer spend category information.
 * 
 * <p>This DTO represents a customer along with their spend category classification
 * based on individual transaction amounts. The category is determined by analyzing
 * each transaction and finding the maximum transaction amount.
 * 
 * <p><b>Classification Logic:</b>
 * <ul>
 *   <li>HIGH_SPEND: Any single transaction &gt; 50,000</li>
 *   <li>MID_SPEND: Any single transaction between 20,000 and 50,000</li>
 *   <li>LOW_SPEND: All transactions &lt; 20,000</li>
 * </ul>
 * 
 * <p><b>Java 8 Streams Learning:</b> This DTO is populated using stream operations
 * such as {@code groupingBy}, {@code mapping}, and {@code maxBy} to categorize
 * customers based on their transaction patterns.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see SpendCategory
 * @see com.ecommerce.order.service.CustomerStatsService#getCustomerSpendCategories()
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSpendCategoryDTO {
    
    /**
     * Unique identifier of the customer.
     */
    private Long customerId;
    
    /**
     * The spend category classification for this customer.
     * Determined by analyzing individual transaction amounts.
     */
    private SpendCategory spendCategory;
    
    /**
     * Human-readable description of the spend category.
     */
    private String categoryDescription;
    
    /**
     * The highest single transaction amount for this customer.
     * Used to determine the spend category.
     */
    private BigDecimal highestTransactionAmount;
    
    /**
     * Total number of transactions for this customer.
     */
    private Integer totalTransactions;
}

