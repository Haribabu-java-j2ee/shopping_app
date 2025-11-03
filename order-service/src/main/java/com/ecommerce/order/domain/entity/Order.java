package com.ecommerce.order.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing customer orders.
 * 
 * <p>Implements the Aggregate Root pattern from Domain-Driven Design.
 * Manages the lifecycle of order items and maintains consistency.
 * 
 * <p><b>Design Patterns Used:</b></p>
 * <ul>
 *   <li>Aggregate Root: Order aggregates OrderItems</li>
 *   <li>State Pattern: Order status transitions with validation</li>
 *   <li>Builder Pattern: Flexible object construction</li>
 * </ul>
 * 
 * <p><b>State Transitions:</b></p>
 * PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
 *         -> CANCELLED (from any state except DELIVERED)
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "orders",
       indexes = {
           @Index(name = "idx_customer_id", columnList = "customer_id"),
           @Index(name = "idx_order_status", columnList = "status"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Order number - unique, human-readable identifier.
     * Format: ORD-{timestamp}-{random}
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    /**
     * Customer ID from Auth Service.
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    /**
     * Current status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    
    /**
     * Total amount before discounts and taxes.
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    /**
     * Tax amount.
     */
    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    /**
     * Shipping cost.
     */
    @Column(name = "shipping_cost", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;
    
    /**
     * Discount amount.
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    /**
     * Total amount (subtotal + tax + shipping - discount).
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    /**
     * Payment method (CREDIT_CARD, DEBIT_CARD, UPI, etc.).
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    /**
     * Payment transaction ID from payment gateway.
     */
    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;
    
    /**
     * Shipping address.
     */
    @Embedded
    private Address shippingAddress;
    
    /**
     * Billing address.
     */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip_code")),
        @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private Address billingAddress;
    
    /**
     * Order items - one-to-many relationship.
     * Cascade all operations and orphan removal.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    /**
     * Special instructions or notes for the order.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Timestamp when order was confirmed.
     */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    /**
     * Timestamp when order was shipped.
     */
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    /**
     * Timestamp when order was delivered.
     */
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    /**
     * Timestamp when order was cancelled.
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    /**
     * Reason for cancellation.
     */
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    /**
     * Timestamp when the order was created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the order was last modified.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Adds an order item to this order.
     * Maintains bidirectional relationship.
     * 
     * @param item Order item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    /**
     * Removes an order item from this order.
     * 
     * @param item Order item to remove
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
    
    /**
     * Calculates and updates the total amount.
     * Total = Subtotal + Tax + Shipping - Discount
     */
    public void calculateTotalAmount() {
        this.totalAmount = subtotal
                .add(taxAmount)
                .add(shippingCost)
                .subtract(discountAmount);
    }
    
    /**
     * Transitions order to CONFIRMED status.
     * Implements State Pattern for valid transitions.
     * 
     * @throws IllegalStateException if transition is invalid
     */
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                "Order can only be confirmed from PENDING status"
            );
        }
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
    
    /**
     * Transitions order to PROCESSING status.
     * 
     * @throws IllegalStateException if transition is invalid
     */
    public void startProcessing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Order can only be processed from CONFIRMED status"
            );
        }
        this.status = OrderStatus.PROCESSING;
    }
    
    /**
     * Transitions order to SHIPPED status.
     * 
     * @throws IllegalStateException if transition is invalid
     */
    public void ship() {
        if (status != OrderStatus.PROCESSING) {
            throw new IllegalStateException(
                "Order can only be shipped from PROCESSING status"
            );
        }
        this.status = OrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }
    
    /**
     * Transitions order to DELIVERED status.
     * 
     * @throws IllegalStateException if transition is invalid
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException(
                "Order can only be delivered from SHIPPED status"
            );
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }
    
    /**
     * Cancels the order with a reason.
     * Can be cancelled from any status except DELIVERED.
     * 
     * @param reason Reason for cancellation
     * @throws IllegalStateException if order is already delivered
     */
    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                "Delivered orders cannot be cancelled"
            );
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                "Order is already cancelled"
            );
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }
    
    /**
     * Checks if the order can be cancelled.
     * 
     * @return true if order can be cancelled
     */
    public boolean isCancellable() {
        return status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED;
    }
}





