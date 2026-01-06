package com.ecommerce.order.service;

import com.ecommerce.order.dto.CustomerSpendCategoryDTO;
import com.ecommerce.order.dto.CustomerTotalValueDTO;
import com.ecommerce.order.dto.CustomerVolumeCategoryDTO;

import java.util.List;

/**
 * Service interface for customer statistics and analytics operations.
 * 
 * <p>This service provides APIs for analyzing customer transaction patterns
 * using Java 8 Streams. All data fetching is done at the repository level,
 * and all computations (aggregations, categorizations) are performed using
 * Java 8 Stream operations for learning purposes.
 * 
 * <p><b>Key Java 8 Stream Concepts Demonstrated:</b>
 * <ul>
 *   <li>{@code Collectors.groupingBy} - Grouping orders by customer</li>
 *   <li>{@code Collectors.reducing} - Aggregating values within groups</li>
 *   <li>{@code Collectors.mapping} - Transforming elements within groups</li>
 *   <li>{@code Stream.max/min} - Finding extremes in a stream</li>
 *   <li>{@code Collectors.toMap} - Creating maps from streams</li>
 *   <li>{@code Optional} handling - Safe value extraction</li>
 * </ul>
 * 
 * <p><b>API Categories:</b>
 * <ol>
 *   <li>Spend Category - Based on individual transaction amounts</li>
 *   <li>Volume Category - Based on total aggregate spending</li>
 *   <li>Total Value - Aggregated transaction values per customer</li>
 * </ol>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see CustomerSpendCategoryDTO
 * @see CustomerVolumeCategoryDTO
 * @see CustomerTotalValueDTO
 */
public interface CustomerStatsService {
    
    /**
     * Gets customer spend categories based on individual transaction amounts.
     * 
     * <p><b>Classification Logic:</b>
     * <ul>
     *   <li>HIGH_SPEND: Any single transaction amount &gt; 50,000</li>
     *   <li>MID_SPEND: Any single transaction between 20,000 and 50,000 (inclusive of 20,000)</li>
     *   <li>LOW_SPEND: All transactions &lt; 20,000</li>
     * </ul>
     * 
     * <p><b>Stream Operations Used:</b>
     * <pre>{@code
     * orders.stream()
     *       .collect(Collectors.groupingBy(Order::getCustomerId))
     *       .entrySet().stream()
     *       .map(entry -> {
     *           BigDecimal maxTransaction = entry.getValue().stream()
     *               .map(Order::getTotalAmount)
     *               .max(BigDecimal::compareTo)
     *               .orElse(BigDecimal.ZERO);
     *           // Determine category based on maxTransaction
     *       })
     *       .collect(Collectors.toList());
     * }</pre>
     * 
     * @return List of CustomerSpendCategoryDTO with spend categories for all customers
     */
    List<CustomerSpendCategoryDTO> getCustomerSpendCategories();
    
    /**
     * Gets customer volume categories based on total aggregate transaction value.
     * 
     * <p><b>Classification Logic:</b>
     * <ul>
     *   <li>HIGH_VOLUME: Total order value &gt; 50,000</li>
     *   <li>MID_VOLUME: Total order value between 20,000 and 50,000 (inclusive of 20,000)</li>
     *   <li>LOW_VOLUME: Total order value &lt; 20,000</li>
     * </ul>
     * 
     * <p><b>Stream Operations Used:</b>
     * <pre>{@code
     * orders.stream()
     *       .collect(Collectors.groupingBy(
     *           Order::getCustomerId,
     *           Collectors.reducing(
     *               BigDecimal.ZERO,
     *               Order::getTotalAmount,
     *               BigDecimal::add
     *           )
     *       ))
     *       .entrySet().stream()
     *       .map(entry -> // Create DTO with volume category)
     *       .collect(Collectors.toList());
     * }</pre>
     * 
     * @return List of CustomerVolumeCategoryDTO with volume categories for all customers
     */
    List<CustomerVolumeCategoryDTO> getCustomerVolumeCategories();
    
    /**
     * Gets aggregated total transaction values for all customers.
     * 
     * <p><b>Computation:</b>
     * For each customer, aggregates all order amounts to compute:
     * <ul>
     *   <li>Total transaction value</li>
     *   <li>Order count</li>
     *   <li>Average order value</li>
     * </ul>
     * 
     * <p><b>Stream Operations Used:</b>
     * <pre>{@code
     * orders.stream()
     *       .collect(Collectors.groupingBy(Order::getCustomerId))
     *       .entrySet().stream()
     *       .map(entry -> {
     *           BigDecimal total = entry.getValue().stream()
     *               .map(Order::getTotalAmount)
     *               .reduce(BigDecimal.ZERO, BigDecimal::add);
     *           // Create DTO with computed values
     *       })
     *       .collect(Collectors.toList());
     * }</pre>
     * 
     * @return List of CustomerTotalValueDTO with aggregated values for all customers
     */
    List<CustomerTotalValueDTO> getCustomerTotalValues();
}

