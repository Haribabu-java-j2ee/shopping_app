package com.ecommerce.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for customer total transaction value.
 * 
 * <p>This DTO represents a customer along with their aggregated total
 * transaction value computed by summing all order amounts.
 * 
 * <p><b>Computation Logic:</b>
 * For each customer, sum all {@code totalAmount} values from their orders
 * to calculate the cumulative spending.
 * 
 * <p><b>Java 8 Streams Learning:</b> This DTO is populated using stream operations
 * such as {@code groupingBy} with {@code reducing} collector to aggregate
 * transaction amounts per customer.
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
 *       ));
 * }</pre>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see com.ecommerce.order.service.CustomerStatsService#getCustomerTotalValues()
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerTotalValueDTO {
    
    /**
     * Unique identifier of the customer.
     */
    private Long customerId;
    
    /**
     * Total aggregated value of all transactions for this customer.
     * Computed by summing totalAmount from all orders.
     */
    private BigDecimal totalValue;
    
    /**
     * Total number of orders contributing to the total value.
     */
    private Integer orderCount;
    
    /**
     * Average order value for this customer.
     * Calculated as totalValue / orderCount.
     */
    private BigDecimal averageOrderValue;
}

