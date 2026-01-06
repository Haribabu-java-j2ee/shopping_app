package com.ecommerce.order.service;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for order operations.
 * 
 * <p>Defines contract for order management business logic.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
public interface OrderService {
    
    /**
     * Creates a new order.
     * Implements Saga pattern for distributed transaction.
     * 
     * @param request Order creation request
     * @return Created order DTO
     */
    OrderDTO createOrder(CreateOrderRequest request);
    
    /**
     * Gets an order by ID.
     * 
     * @param orderId Order ID
     * @return Order DTO
     */
    OrderDTO getOrder(Long orderId);
    
    /**
     * Gets an order by order number.
     * 
     * @param orderNumber Order number
     * @return Order DTO
     */
    OrderDTO getOrderByNumber(String orderNumber);
    
    /**
     * Gets all orders for a customer.
     * 
     * @param customerId Customer ID
     * @param pageable Pagination
     * @return Page of orders
     */
    Page<OrderDTO> getCustomerOrders(Long customerId, Pageable pageable);
    
    /**
     * Cancels an order.
     * 
     * @param orderId Order ID
     * @param reason Cancellation reason
     * @return Updated order DTO
     */
    OrderDTO cancelOrder(Long orderId, String reason);
    
    /**
     * Confirms an order (after payment).
     * 
     * @param orderId Order ID
     * @param transactionId Payment transaction ID
     * @return Updated order DTO
     */
    OrderDTO confirmOrder(Long orderId, String transactionId);
}








