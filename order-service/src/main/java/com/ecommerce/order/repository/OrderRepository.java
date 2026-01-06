package com.ecommerce.order.repository;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity data access.
 * 
 * <p>Provides CRUD operations and custom queries for order management.
 * Implements Repository Pattern for data access abstraction.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Finds an order by order number.
     * 
     * @param orderNumber Order number
     * @return Optional containing order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Finds all orders for a specific customer.
     * 
     * @param customerId Customer ID
     * @param pageable Pagination information
     * @return Page of orders
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Finds orders by status.
     * 
     * @param status Order status
     * @param pageable Pagination information
     * @return Page of orders
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * Finds orders for a customer with specific status.
     * 
     * @param customerId Customer ID
     * @param status Order status
     * @param pageable Pagination information
     * @return Page of orders
     */
    Page<Order> findByCustomerIdAndStatus(
        Long customerId, 
        OrderStatus status, 
        Pageable pageable
    );
    
    /**
     * Finds orders created within a date range.
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Finds pending orders older than specified time.
     * Used for order timeout processing.
     * 
     * @param olderThan Timestamp threshold
     * @return List of pending orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' " +
           "AND o.createdAt < :olderThan")
    List<Order> findPendingOrdersOlderThan(@Param("olderThan") LocalDateTime olderThan);
    
    /**
     * Counts orders by customer and status.
     * 
     * @param customerId Customer ID
     * @param status Order status
     * @return Count of orders
     */
    long countByCustomerIdAndStatus(Long customerId, OrderStatus status);
    
    /**
     * Checks if an order exists with the given order number.
     * 
     * @param orderNumber Order number
     * @return true if exists
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * Fetches all orders with their items eagerly loaded.
     * 
     * <p>This method uses a JPQL JOIN FETCH to load order items in a single query,
     * avoiding N+1 query problem when accessing items for customer statistics.
     * 
     * <p><b>Note:</b> This query returns all orders from the database. For large
     * datasets, consider using pagination or filtering by date range.
     * 
     * @return List of all orders with items eagerly loaded
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items")
    List<Order> findAllWithItems();
    
    /**
     * Finds all orders for a specific customer with items eagerly loaded.
     * 
     * @param customerId Customer ID
     * @return List of orders for the customer with items
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.customerId = :customerId")
    List<Order> findByCustomerIdWithItems(@Param("customerId") Long customerId);
}








