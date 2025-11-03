package com.ecommerce.order.domain.entity;

/**
 * Enumeration of order statuses.
 * 
 * <p>Defines the lifecycle states of an order.
 * 
 * <p><b>State Flow:</b></p>
 * <pre>
 * PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
 *         -> CANCELLED (from any state except DELIVERED)
 * </pre>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet confirmed.
     * Payment may still be pending.
     */
    PENDING,
    
    /**
     * Order has been confirmed and payment received.
     * Ready for processing.
     */
    CONFIRMED,
    
    /**
     * Order is being processed (items being picked and packed).
     */
    PROCESSING,
    
    /**
     * Order has been shipped to customer.
     */
    SHIPPED,
    
    /**
     * Order has been delivered to customer.
     * Final successful state.
     */
    DELIVERED,
    
    /**
     * Order has been cancelled.
     * Can be initiated by customer or system.
     */
    CANCELLED
}





