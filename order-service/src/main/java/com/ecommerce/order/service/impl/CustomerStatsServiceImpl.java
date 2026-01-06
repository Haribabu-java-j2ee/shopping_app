package com.ecommerce.order.service.impl;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.SpendCategory;
import com.ecommerce.order.domain.entity.VolumeCategory;
import com.ecommerce.order.dto.CustomerSpendCategoryDTO;
import com.ecommerce.order.dto.CustomerTotalValueDTO;
import com.ecommerce.order.dto.CustomerVolumeCategoryDTO;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.CustomerStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerStatsService with Java 8 Streams.
 * 
 * <p>This implementation demonstrates various Java 8 Stream operations for
 * learning purposes. All data is fetched from the database layer, and all
 * computations (aggregations, categorizations, transformations) are performed
 * using Java 8 Stream API.
 * 
 * <p><b>Java 8 Stream Concepts Demonstrated:</b>
 * <ul>
 *   <li>{@code Collectors.groupingBy()} - Grouping elements by a classifier function</li>
 *   <li>{@code Collectors.reducing()} - Reducing elements to a single value within groups</li>
 *   <li>{@code Collectors.mapping()} - Mapping elements before collecting</li>
 *   <li>{@code Stream.map()} - Transforming elements</li>
 *   <li>{@code Stream.max()/min()} - Finding maximum/minimum elements</li>
 *   <li>{@code Stream.reduce()} - Reducing stream to a single value</li>
 *   <li>{@code Optional.orElse()} - Safe default value extraction</li>
 *   <li>{@code Stream.sorted()} - Sorting stream elements</li>
 *   <li>{@code Stream.collect()} - Mutable reduction to collections</li>
 *   <li>{@code Comparator} - Custom comparisons for BigDecimal</li>
 * </ul>
 * 
 * <p><b>Threshold Constants:</b>
 * <ul>
 *   <li>HIGH_THRESHOLD: 50,000 - Above this is HIGH category</li>
 *   <li>MID_THRESHOLD: 20,000 - Above this (up to HIGH) is MID category</li>
 * </ul>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see CustomerStatsService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerStatsServiceImpl implements CustomerStatsService {
    
    /**
     * Threshold for HIGH spend/volume category.
     * Transactions or totals above this value are classified as HIGH.
     */
    private static final BigDecimal HIGH_THRESHOLD = new BigDecimal("50000");
    
    /**
     * Threshold for MID spend/volume category.
     * Transactions or totals between this and HIGH_THRESHOLD are classified as MID.
     * Below this value is classified as LOW.
     */
    private static final BigDecimal MID_THRESHOLD = new BigDecimal("20000");
    
    /**
     * Repository for fetching order data.
     */
    private final OrderRepository orderRepository;
    
    /**
     * {@inheritDoc}
     * 
     * <p><b>Implementation Details:</b>
     * <ol>
     *   <li>Fetch all orders from database using {@link OrderRepository#findAllWithItems()}</li>
     *   <li>Group orders by customerId using {@code Collectors.groupingBy()}</li>
     *   <li>For each customer group, find the maximum transaction amount using {@code Stream.max()}</li>
     *   <li>Determine spend category based on max transaction using {@link #determineSpendCategory(BigDecimal)}</li>
     *   <li>Build and return list of DTOs sorted by customerId</li>
     * </ol>
     * 
     * <p><b>Stream Pipeline:</b>
     * <pre>{@code
     * orders.stream()
     *       .collect(groupingBy(Order::getCustomerId))  // Step 1: Group by customer
     *       .entrySet().stream()                         // Step 2: Stream entries
     *       .map(entry -> {                              // Step 3: Transform to DTO
     *           maxAmount = findMaxTransaction(orders);
     *           category = determineCategory(maxAmount);
     *           return buildDTO(customerId, category, maxAmount);
     *       })
     *       .sorted(by customerId)                       // Step 4: Sort results
     *       .collect(toList());                          // Step 5: Collect to list
     * }</pre>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerSpendCategoryDTO> getCustomerSpendCategories() {
        log.info("Fetching customer spend categories based on individual transaction amounts");
        
        // Step 1: Fetch all orders from database
        List<Order> allOrders = orderRepository.findAllWithItems();
        log.debug("Fetched {} orders from database", allOrders.size());
        
        // Step 2: Group orders by customerId using Collectors.groupingBy()
        // This creates Map<Long, List<Order>> where key is customerId
        Map<Long, List<Order>> ordersByCustomer = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getCustomerId));
        
        log.debug("Grouped orders for {} unique customers", ordersByCustomer.size());
        
        // Step 3: Transform each customer's orders into CustomerSpendCategoryDTO
        // Using entrySet().stream() to iterate over the grouped map
        List<CustomerSpendCategoryDTO> result = ordersByCustomer.entrySet().stream()
                .map(entry -> {
                    Long customerId = entry.getKey();
                    List<Order> customerOrders = entry.getValue();
                    
                    // Find the maximum transaction amount using Stream.max()
                    // with Comparator for BigDecimal comparison
                    BigDecimal maxTransactionAmount = customerOrders.stream()
                            .map(Order::getTotalAmount)
                            .max(Comparator.naturalOrder())
                            .orElse(BigDecimal.ZERO);
                    
                    // Determine spend category based on max transaction
                    SpendCategory spendCategory = determineSpendCategory(maxTransactionAmount);
                    
                    // Build and return DTO using Builder pattern
                    return CustomerSpendCategoryDTO.builder()
                            .customerId(customerId)
                            .spendCategory(spendCategory)
                            .categoryDescription(spendCategory.getDescription())
                            .highestTransactionAmount(maxTransactionAmount)
                            .totalTransactions(customerOrders.size())
                            .build();
                })
                // Step 4: Sort by customerId for consistent output
                .sorted(Comparator.comparing(CustomerSpendCategoryDTO::getCustomerId))
                // Step 5: Collect to List
                .collect(Collectors.toList());
        
        log.info("Computed spend categories for {} customers", result.size());
        return result;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p><b>Implementation Details:</b>
     * <ol>
     *   <li>Fetch all orders from database</li>
     *   <li>Group and aggregate total order value per customer using 
     *       {@code Collectors.groupingBy()} with {@code Collectors.reducing()}</li>
     *   <li>Determine volume category based on total value using {@link #determineVolumeCategory(BigDecimal)}</li>
     *   <li>Build and return list of DTOs sorted by customerId</li>
     * </ol>
     * 
     * <p><b>Stream Pipeline:</b>
     * <pre>{@code
     * orders.stream()
     *       .collect(groupingBy(
     *           Order::getCustomerId,
     *           reducing(ZERO, Order::getTotalAmount, BigDecimal::add)
     *       ))  // Creates Map<Long, BigDecimal> with totals
     *       .entrySet().stream()
     *       .map(entry -> buildDTO(customerId, total, category))
     *       .collect(toList());
     * }</pre>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerVolumeCategoryDTO> getCustomerVolumeCategories() {
        log.info("Fetching customer volume categories based on total transaction value");
        
        // Step 1: Fetch all orders from database
        List<Order> allOrders = orderRepository.findAllWithItems();
        log.debug("Fetched {} orders from database", allOrders.size());
        
        // Step 2: Group by customer and calculate total order value
        // Using Collectors.groupingBy() with downstream Collectors.reducing()
        // This demonstrates combining collectors for aggregation
        Map<Long, BigDecimal> totalValueByCustomer = allOrders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.reducing(
                                BigDecimal.ZERO,           // Identity value
                                Order::getTotalAmount,     // Mapper function
                                BigDecimal::add            // Reducer function
                        )
                ));
        
        // Also calculate order counts per customer using separate stream
        // Demonstrates Collectors.counting() as downstream collector
        Map<Long, Long> orderCountByCustomer = allOrders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomerId,
                        Collectors.counting()
                ));
        
        log.debug("Calculated totals for {} unique customers", totalValueByCustomer.size());
        
        // Step 3: Transform to DTOs with volume category
        List<CustomerVolumeCategoryDTO> result = totalValueByCustomer.entrySet().stream()
                .map(entry -> {
                    Long customerId = entry.getKey();
                    BigDecimal totalValue = entry.getValue();
                    
                    // Determine volume category based on total order value
                    VolumeCategory volumeCategory = determineVolumeCategory(totalValue);
                    
                    // Get order count for this customer
                    Integer orderCount = orderCountByCustomer.get(customerId).intValue();
                    
                    return CustomerVolumeCategoryDTO.builder()
                            .customerId(customerId)
                            .volumeCategory(volumeCategory)
                            .categoryDescription(volumeCategory.getDescription())
                            .totalOrderValue(totalValue)
                            .totalOrders(orderCount)
                            .build();
                })
                .sorted(Comparator.comparing(CustomerVolumeCategoryDTO::getCustomerId))
                .collect(Collectors.toList());
        
        log.info("Computed volume categories for {} customers", result.size());
        return result;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p><b>Implementation Details:</b>
     * <ol>
     *   <li>Fetch all orders from database</li>
     *   <li>Group orders by customerId</li>
     *   <li>For each customer, compute: total value, order count, average order value</li>
     *   <li>Build DTOs sorted by total value (descending) for easy analysis</li>
     * </ol>
     * 
     * <p><b>Stream Operations Highlighted:</b>
     * <ul>
     *   <li>{@code reduce()} - Summing BigDecimal values</li>
     *   <li>{@code count()} via {@code size()} - Counting orders</li>
     *   <li>{@code divide()} with RoundingMode - Computing averages</li>
     *   <li>{@code sorted()} with reversed comparator - Descending sort</li>
     * </ul>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerTotalValueDTO> getCustomerTotalValues() {
        log.info("Fetching customer total transaction values");
        
        // Step 1: Fetch all orders
        List<Order> allOrders = orderRepository.findAllWithItems();
        log.debug("Fetched {} orders from database", allOrders.size());
        
        // Step 2: Group orders by customerId
        Map<Long, List<Order>> ordersByCustomer = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getCustomerId));
        
        // Step 3: Transform each customer's orders into CustomerTotalValueDTO
        // Demonstrating comprehensive stream operations for aggregation
        List<CustomerTotalValueDTO> result = ordersByCustomer.entrySet().stream()
                .map(entry -> {
                    Long customerId = entry.getKey();
                    List<Order> customerOrders = entry.getValue();
                    
                    // Calculate total value using reduce()
                    // This is an alternative to Collectors.reducing()
                    BigDecimal totalValue = customerOrders.stream()
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Get order count
                    int orderCount = customerOrders.size();
                    
                    // Calculate average order value
                    // Using BigDecimal.divide() with RoundingMode for precision
                    BigDecimal averageOrderValue = orderCount > 0
                            ? totalValue.divide(
                                    new BigDecimal(orderCount), 
                                    2,                          // Scale (decimal places)
                                    RoundingMode.HALF_UP        // Rounding mode
                              )
                            : BigDecimal.ZERO;
                    
                    return CustomerTotalValueDTO.builder()
                            .customerId(customerId)
                            .totalValue(totalValue)
                            .orderCount(orderCount)
                            .averageOrderValue(averageOrderValue)
                            .build();
                })
                // Sort by total value descending - highest spenders first
                // Demonstrates reversed() for descending order
                .sorted(Comparator.comparing(
                        CustomerTotalValueDTO::getTotalValue,
                        Comparator.reverseOrder()
                ))
                .collect(Collectors.toList());
        
        log.info("Computed total values for {} customers", result.size());
        return result;
    }
    
    /**
     * Determines the spend category based on the maximum single transaction amount.
     * 
     * <p><b>Classification Rules:</b>
     * <ul>
     *   <li>If maxAmount &gt; 50,000 → HIGH_SPEND</li>
     *   <li>If maxAmount &gt;= 20,000 and &lt;= 50,000 → MID_SPEND</li>
     *   <li>If maxAmount &lt; 20,000 → LOW_SPEND</li>
     * </ul>
     * 
     * @param maxTransactionAmount The highest single transaction amount for a customer
     * @return SpendCategory classification (HIGH_SPEND, MID_SPEND, or LOW_SPEND)
     */
    private SpendCategory determineSpendCategory(BigDecimal maxTransactionAmount) {
        if (maxTransactionAmount.compareTo(HIGH_THRESHOLD) > 0) {
            return SpendCategory.HIGH_SPEND;
        } else if (maxTransactionAmount.compareTo(MID_THRESHOLD) >= 0) {
            return SpendCategory.MID_SPEND;
        } else {
            return SpendCategory.LOW_SPEND;
        }
    }
    
    /**
     * Determines the volume category based on total aggregate transaction value.
     * 
     * <p><b>Classification Rules:</b>
     * <ul>
     *   <li>If totalValue &gt; 50,000 → HIGH_VOLUME</li>
     *   <li>If totalValue &gt;= 20,000 and &lt;= 50,000 → MID_VOLUME</li>
     *   <li>If totalValue &lt; 20,000 → LOW_VOLUME</li>
     * </ul>
     * 
     * @param totalOrderValue The total aggregate order value for a customer
     * @return VolumeCategory classification (HIGH_VOLUME, MID_VOLUME, or LOW_VOLUME)
     */
    private VolumeCategory determineVolumeCategory(BigDecimal totalOrderValue) {
        if (totalOrderValue.compareTo(HIGH_THRESHOLD) > 0) {
            return VolumeCategory.HIGH_VOLUME;
        } else if (totalOrderValue.compareTo(MID_THRESHOLD) >= 0) {
            return VolumeCategory.MID_VOLUME;
        } else {
            return VolumeCategory.LOW_VOLUME;
        }
    }
}

