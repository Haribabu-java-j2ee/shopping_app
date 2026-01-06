package com.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new order.
 * 
 * <p>Contains all required information to create an order including
 * items, addresses, and payment details.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    /**
     * Customer ID placing the order.
     */
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    /**
     * List of items to order.
     */
    @NotEmpty(message = "Order must contain at least one item")
    @Size(max = 100, message = "Maximum 100 items per order")
    @Valid
    private List<OrderItemDTO> items;
    
    /**
     * Shipping address.
     */
    @NotNull(message = "Shipping address is required")
    @Valid
    private AddressDTO shippingAddress;
    
    /**
     * Billing address.
     * If not provided, shipping address will be used.
     */
    @Valid
    private AddressDTO billingAddress;
    
    /**
     * Payment method (CREDIT_CARD, DEBIT_CARD, UPI, etc.).
     */
    @NotNull(message = "Payment method is required")
    private String paymentMethod;
    
    /**
     * Special instructions or notes.
     */
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}








