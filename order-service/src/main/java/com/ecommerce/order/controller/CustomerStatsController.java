package com.ecommerce.order.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.order.dto.CustomerSpendCategoryDTO;
import com.ecommerce.order.dto.CustomerTotalValueDTO;
import com.ecommerce.order.dto.CustomerVolumeCategoryDTO;
import com.ecommerce.order.service.CustomerStatsService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Customer Statistics APIs.
 * 
 * <p>This controller provides endpoints for analyzing customer transaction patterns
 * and categorizing customers based on their spending behavior. All computations
 * are performed using Java 8 Streams in the service layer.
 * 
 * <p><b>Available Endpoints:</b>
 * <ul>
 *   <li>{@code GET /api/customers/stats/spend-category} - Customer spend categories based on single transactions</li>
 *   <li>{@code GET /api/customers/stats/volume-category} - Customer volume categories based on total spending</li>
 *   <li>{@code GET /api/customers/stats/total-value} - Aggregated total transaction values per customer</li>
 * </ul>
 * 
 * <p><b>Response Format:</b>
 * All endpoints return responses wrapped in {@link ApiResponse} for consistent API structure.
 * 
 * <p><b>Java 8 Streams Learning:</b>
 * <ul>
 *   <li>Spend Category API - Demonstrates {@code max()}, {@code groupingBy()}</li>
 *   <li>Volume Category API - Demonstrates {@code reducing()}, aggregation</li>
 *   <li>Total Value API - Demonstrates {@code reduce()}, average calculation</li>
 * </ul>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see CustomerStatsService
 */
@Slf4j
@RestController
@RequestMapping("/api/customers/stats")
@RequiredArgsConstructor
public class CustomerStatsController {
    
    /**
     * Service for customer statistics computations.
     */
    private final CustomerStatsService customerStatsService;
    
    /**
     * Gets customer spend categories based on individual transaction amounts.
     * 
     * <p><b>API Description:</b>
     * This endpoint analyzes each customer's transactions and categorizes them
     * based on their highest single transaction amount:
     * <ul>
     *   <li>HIGH_SPEND: Any transaction &gt; 50,000</li>
     *   <li>MID_SPEND: Any transaction between 20,000 - 50,000</li>
     *   <li>LOW_SPEND: All transactions &lt; 20,000</li>
     * </ul>
     * 
     * <p><b>Java 8 Streams Concepts:</b>
     * <pre>{@code
     * // Grouping orders by customer
     * Collectors.groupingBy(Order::getCustomerId)
     * 
     * // Finding max transaction
     * stream.map(Order::getTotalAmount).max(Comparator.naturalOrder())
     * }</pre>
     * 
     * <p><b>Example Response:</b>
     * <pre>{@code
     * {
     *   "success": true,
     *   "message": "Customer spend categories retrieved successfully",
     *   "data": [
     *     {
     *       "customerId": 1,
     *       "spendCategory": "HIGH_SPEND",
     *       "categoryDescription": "High Spend User",
     *       "highestTransactionAmount": 75000.00,
     *       "totalTransactions": 5
     *     }
     *   ]
     * }
     * }</pre>
     * 
     * @return ResponseEntity containing list of CustomerSpendCategoryDTO
     */
    @GetMapping("/spend-category")
    @Timed(value = "customer.stats.spend.category", description = "Time to compute customer spend categories")
    public ResponseEntity<ApiResponse<List<CustomerSpendCategoryDTO>>> getCustomerSpendCategories() {
        log.info("REST request to get customer spend categories");
        
        List<CustomerSpendCategoryDTO> spendCategories = customerStatsService.getCustomerSpendCategories();
        
        ApiResponse<List<CustomerSpendCategoryDTO>> response = ApiResponse.success(
                "Customer spend categories retrieved successfully",
                spendCategories
        );
        
        log.debug("Returning {} customer spend categories", spendCategories.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Gets customer volume categories based on total aggregate transaction value.
     * 
     * <p><b>API Description:</b>
     * This endpoint calculates each customer's total transaction value by summing
     * all their orders, then categorizes them:
     * <ul>
     *   <li>HIGH_VOLUME: Total value &gt; 50,000</li>
     *   <li>MID_VOLUME: Total value between 20,000 - 50,000</li>
     *   <li>LOW_VOLUME: Total value &lt; 20,000</li>
     * </ul>
     * 
     * <p><b>Java 8 Streams Concepts:</b>
     * <pre>{@code
     * // Grouping with aggregation using reducing collector
     * Collectors.groupingBy(
     *     Order::getCustomerId,
     *     Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
     * )
     * 
     * // Counting using downstream collector
     * Collectors.groupingBy(Order::getCustomerId, Collectors.counting())
     * }</pre>
     * 
     * <p><b>Example Response:</b>
     * <pre>{@code
     * {
     *   "success": true,
     *   "message": "Customer volume categories retrieved successfully",
     *   "data": [
     *     {
     *       "customerId": 1,
     *       "volumeCategory": "HIGH_VOLUME",
     *       "categoryDescription": "High Volume Spend Customer",
     *       "totalOrderValue": 125000.00,
     *       "totalOrders": 8
     *     }
     *   ]
     * }
     * }</pre>
     * 
     * @return ResponseEntity containing list of CustomerVolumeCategoryDTO
     */
    @GetMapping("/volume-category")
    @Timed(value = "customer.stats.volume.category", description = "Time to compute customer volume categories")
    public ResponseEntity<ApiResponse<List<CustomerVolumeCategoryDTO>>> getCustomerVolumeCategories() {
        log.info("REST request to get customer volume categories");
        
        List<CustomerVolumeCategoryDTO> volumeCategories = customerStatsService.getCustomerVolumeCategories();
        
        ApiResponse<List<CustomerVolumeCategoryDTO>> response = ApiResponse.success(
                "Customer volume categories retrieved successfully",
                volumeCategories
        );
        
        log.debug("Returning {} customer volume categories", volumeCategories.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Gets aggregated total transaction values for all customers.
     * 
     * <p><b>API Description:</b>
     * This endpoint computes comprehensive transaction statistics for each customer:
     * <ul>
     *   <li>Total transaction value (sum of all orders)</li>
     *   <li>Total order count</li>
     *   <li>Average order value</li>
     * </ul>
     * 
     * <p><b>Java 8 Streams Concepts:</b>
     * <pre>{@code
     * // Summing using reduce
     * stream.map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
     * 
     * // Sorting with reversed comparator
     * stream.sorted(Comparator.comparing(dto::getTotalValue, Comparator.reverseOrder()))
     * }</pre>
     * 
     * <p><b>Example Response:</b>
     * <pre>{@code
     * {
     *   "success": true,
     *   "message": "Customer total values retrieved successfully",
     *   "data": [
     *     {
     *       "customerId": 1,
     *       "totalValue": 125000.00,
     *       "orderCount": 8,
     *       "averageOrderValue": 15625.00
     *     }
     *   ]
     * }
     * }</pre>
     * 
     * @return ResponseEntity containing list of CustomerTotalValueDTO sorted by total value descending
     */
    @GetMapping("/total-value")
    @Timed(value = "customer.stats.total.value", description = "Time to compute customer total values")
    public ResponseEntity<ApiResponse<List<CustomerTotalValueDTO>>> getCustomerTotalValues() {
        log.info("REST request to get customer total transaction values");
        
        List<CustomerTotalValueDTO> totalValues = customerStatsService.getCustomerTotalValues();
        
        ApiResponse<List<CustomerTotalValueDTO>> response = ApiResponse.success(
                "Customer total values retrieved successfully",
                totalValues
        );
        
        log.debug("Returning {} customer total values", totalValues.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint for customer stats service.
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = ApiResponse.success(
                "Customer stats service is running",
                "healthy"
        );
        return ResponseEntity.ok(response);
    }
}

