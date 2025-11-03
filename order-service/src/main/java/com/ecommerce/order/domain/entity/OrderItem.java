package com.ecommerce.order.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderItem entity representing individual items in an order.
 * 
 * <p>This is an entity within the Order aggregate. It cannot exist
 * independently without an Order (composition relationship).
 * 
 * <p><b>Design Pattern:</b> Value Object Pattern - OrderItem is part of
 * the Order aggregate and has no independent lifecycle.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "order_items",
       indexes = {
           @Index(name = "idx_order_id", columnList = "order_id"),
           @Index(name = "idx_product_id", columnList = "product_id")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    /**
     * Unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the parent order.
     * Many-to-one relationship with Order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    /**
     * Product ID from the product catalog.
     */
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    /**
     * Product name (denormalized for historical reference).
     * Stored here in case product details change after order.
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    
    /**
     * Product SKU (Stock Keeping Unit).
     */
    @Column(name = "sku", length = 100)
    private String sku;
    
    /**
     * Quantity ordered.
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Unit price at the time of order.
     * Price is denormalized to preserve historical pricing.
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    /**
     * Total price for this item (quantity * unitPrice).
     */
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    /**
     * Discount applied to this item.
     */
    @Column(name = "discount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    /**
     * Tax amount for this item.
     */
    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    /**
     * Calculates and updates the total price.
     * Total = (quantity * unitPrice) - discount + tax
     */
    public void calculateTotalPrice() {
        BigDecimal subtotal = unitPrice.multiply(new BigDecimal(quantity));
        this.totalPrice = subtotal.subtract(discount).add(taxAmount);
    }
}





