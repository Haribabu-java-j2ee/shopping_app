package com.ecommerce.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for order item information.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    /**
     * Product ID from catalog.
     */
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    /**
     * Product name.
     */
    @NotBlank(message = "Product name is required")
    private String productName;
    
    /**
     * Product SKU.
     */
    private String sku;
    
    /**
     * Quantity to order.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    /**
     * Unit price.
     */
    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    /**
     * Total price for this item.
     */
    private BigDecimal totalPrice;
    
    /**
     * Discount applied.
     */
    private BigDecimal discount;
    
    /**
     * Tax amount.
     */
    private BigDecimal taxAmount;
}








