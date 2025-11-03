package com.ecommerce.common.constants;

/**
 * Centralized Kafka topic names for event-driven communication.
 * 
 * <p>Using constants ensures consistency across all services and
 * prevents typos in topic names.
 * 
 * <p><b>Topic Naming Convention:</b> {service}.{entity}.{action}
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
public final class KafkaTopics {
    
    // Private constructor to prevent instantiation
    private KafkaTopics() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    /**
     * Topic for order creation events.
     * Published when a new order is created.
     */
    public static final String ORDER_CREATED = "order.created";
    
    /**
     * Topic for order update events.
     * Published when an order status changes.
     */
    public static final String ORDER_UPDATED = "order.updated";
    
    /**
     * Topic for order cancellation events.
     * Published when an order is cancelled.
     */
    public static final String ORDER_CANCELLED = "order.cancelled";
    
    /**
     * Topic for user registration events.
     * Published when a new user registers.
     */
    public static final String USER_REGISTERED = "user.registered";
    
    /**
     * Topic for inventory update events.
     * Published when inventory levels change.
     */
    public static final String INVENTORY_UPDATED = "inventory.updated";
    
    /**
     * Topic for payment processing events.
     * Published during payment lifecycle.
     */
    public static final String PAYMENT_PROCESSED = "payment.processed";
    
    /**
     * Dead letter queue for failed events.
     * Failed events are routed here for later analysis.
     */
    public static final String DLQ = "ecommerce.dlq";
}


