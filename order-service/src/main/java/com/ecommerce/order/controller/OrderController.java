package com.ecommerce.order.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.service.OrderService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order management endpoints.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @Timed(value = "order.create", description = "Time taken to create an order")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("Create order request received for customer: {}", request.getCustomerId());
        
        OrderDTO order = orderService.createOrder(request);
        
        ApiResponse<OrderDTO> response = ApiResponse.success(
            "Order created successfully",
            order
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{orderId}")
    @Timed(value = "order.get", description = "Time taken to retrieve an order")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable Long orderId) {
        log.debug("Get order request for ID: {}", orderId);
        
        OrderDTO order = orderService.getOrder(orderId);
        
        ApiResponse<OrderDTO> response = ApiResponse.success(
            "Order retrieved successfully",
            order
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByNumber(
            @PathVariable String orderNumber) {
        log.debug("Get order request for number: {}", orderNumber);
        
        OrderDTO order = orderService.getOrderByNumber(orderNumber);
        
        ApiResponse<OrderDTO> response = ApiResponse.success(
            "Order retrieved successfully",
            order
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{customerId}")
    @Timed(value = "order.list.customer", description = "Time taken to list customer orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getCustomerOrders(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.debug("Get orders for customer: {}", customerId);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderDTO> orders = orderService.getCustomerOrders(customerId, pageable);
        
        ApiResponse<Page<OrderDTO>> response = ApiResponse.success(
            "Orders retrieved successfully",
            orders
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{orderId}/cancel")
    @Timed(value = "order.cancel", description = "Time taken to cancel an order")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody CancelOrderRequest request) {
        log.info("Cancel order request for ID: {}", orderId);
        
        OrderDTO order = orderService.cancelOrder(orderId, request.getReason());
        
        ApiResponse<OrderDTO> response = ApiResponse.success(
            "Order cancelled successfully",
            order
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{orderId}/confirm")
    @Timed(value = "order.confirm", description = "Time taken to confirm an order")
    public ResponseEntity<ApiResponse<OrderDTO>> confirmOrder(
            @PathVariable Long orderId,
            @RequestBody ConfirmOrderRequest request) {
        log.info("Confirm order request for ID: {}", orderId);
        
        OrderDTO order = orderService.confirmOrder(orderId, request.getTransactionId());
        
        ApiResponse<OrderDTO> response = ApiResponse.success(
            "Order confirmed successfully",
            order
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = ApiResponse.success(
            "Order service is running",
            "healthy"
        );
        return ResponseEntity.ok(response);
    }
    
    @lombok.Data
    private static class CancelOrderRequest {
        private String reason;
    }
    
    @lombok.Data
    private static class ConfirmOrderRequest {
        private String transactionId;
    }
}








