package com.ecommerce.order.service.impl;

import com.ecommerce.common.constants.KafkaTopics;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.domain.entity.Address;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of OrderService.
 * 
 * <p>Handles order lifecycle management with event-driven architecture.
 * 
 * <p><b>Design Patterns:</b></p>
 * <ul>
 *   <li>Saga Pattern: Distributed transaction management</li>
 *   <li>Event Sourcing: Order events published to Kafka</li>
 *   <li>Factory Pattern: Order number generation</li>
 * </ul>
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        // Validate request
        validateOrderRequest(request);
        
        // Create order entity
        Order order = buildOrder(request);
        
        // Generate unique order number
        order.setOrderNumber(generateOrderNumber());
        
        // Calculate totals
        calculateOrderTotals(order);
        
        // Save order
        order = orderRepository.save(order);
        log.info("Order created with ID: {} and number: {}", order.getId(), order.getOrderNumber());
        
        // Publish order created event (Saga Pattern - Step 1)
        publishOrderCreatedEvent(order);
        
        return orderMapper.toDTO(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderMapper.toDTO(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderNumber));
        return orderMapper.toDTO(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getCustomerOrders(Long customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(orderMapper::toDTO);
    }
    
    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        if (!order.isCancellable()) {
            throw new BusinessException(
                "Order cannot be cancelled in current status: " + order.getStatus(),
                "ORDER_NOT_CANCELLABLE"
            );
        }
        
        order.cancel(reason);
        order = orderRepository.save(order);
        
        // Publish order cancelled event (Saga Pattern - Compensating transaction)
        publishOrderCancelledEvent(order);
        
        log.info("Order cancelled: {}", orderId);
        return orderMapper.toDTO(order);
    }
    
    @Override
    @Transactional
    public OrderDTO confirmOrder(Long orderId, String transactionId) {
        log.info("Confirming order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        order.setPaymentTransactionId(transactionId);
        order.confirm();
        order = orderRepository.save(order);
        
        // Publish order confirmed event (Saga Pattern - Step 2)
        publishOrderUpdatedEvent(order);
        
        log.info("Order confirmed: {}", orderId);
        return orderMapper.toDTO(order);
    }
    
    /**
     * Validates order creation request.
     */
    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }
        
        // Calculate total and validate minimum order amount
        BigDecimal total = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (total.compareTo(new BigDecimal("10.00")) < 0) {
            throw new BusinessException(
                "Order total must be at least $10.00",
                "MIN_ORDER_AMOUNT_NOT_MET"
            );
        }
    }
    
    /**
     * Builds Order entity from request.
     */
    private Order buildOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(orderMapper.toEntity(request.getShippingAddress()))
                .billingAddress(request.getBillingAddress() != null ? 
                        orderMapper.toEntity(request.getBillingAddress()) : 
                        orderMapper.toEntity(request.getShippingAddress()))
                .notes(request.getNotes())
                .build();
        
        // Add items
        for (OrderItemDTO itemDTO : request.getItems()) {
            OrderItem item = OrderItem.builder()
                    .productId(itemDTO.getProductId())
                    .productName(itemDTO.getProductName())
                    .sku(itemDTO.getSku())
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .discount(itemDTO.getDiscount() != null ? itemDTO.getDiscount() : BigDecimal.ZERO)
                    .taxAmount(itemDTO.getTaxAmount() != null ? itemDTO.getTaxAmount() : BigDecimal.ZERO)
                    .build();
            
            item.calculateTotalPrice();
            order.addItem(item);
        }
        
        return order;
    }
    
    /**
     * Calculates order totals.
     */
    private void calculateOrderTotals(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setSubtotal(subtotal);
        
        // Calculate tax (10% for simplicity)
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10"));
        order.setTaxAmount(tax);
        
        // Calculate shipping (free over $50)
        BigDecimal shipping = subtotal.compareTo(new BigDecimal("50.00")) >= 0 ? 
                BigDecimal.ZERO : new BigDecimal("5.00");
        order.setShippingCost(shipping);
        
        order.setDiscountAmount(BigDecimal.ZERO);
        order.calculateTotalAmount();
    }
    
    /**
     * Generates unique order number.
     * Format: ORD-{timestamp}-{random}
     */
    private String generateOrderNumber() {
        return String.format("ORD-%d-%s", 
                System.currentTimeMillis(), 
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
    
    /**
     * Publishes order created event to Kafka.
     */
    private void publishOrderCreatedEvent(Order order) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderId", order.getId());
            event.put("orderNumber", order.getOrderNumber());
            event.put("customerId", order.getCustomerId());
            event.put("totalAmount", order.getTotalAmount());
            event.put("status", order.getStatus().name());
            event.put("createdAt", order.getCreatedAt());
            
            kafkaTemplate.send(KafkaTopics.ORDER_CREATED, order.getId().toString(), event);
            log.info("Published ORDER_CREATED event for order: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to publish order created event", e);
        }
    }
    
    /**
     * Publishes order updated event to Kafka.
     */
    private void publishOrderUpdatedEvent(Order order) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderId", order.getId());
            event.put("orderNumber", order.getOrderNumber());
            event.put("status", order.getStatus().name());
            event.put("updatedAt", LocalDateTime.now());
            
            kafkaTemplate.send(KafkaTopics.ORDER_UPDATED, order.getId().toString(), event);
            log.info("Published ORDER_UPDATED event for order: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to publish order updated event", e);
        }
    }
    
    /**
     * Publishes order cancelled event to Kafka.
     */
    private void publishOrderCancelledEvent(Order order) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderId", order.getId());
            event.put("orderNumber", order.getOrderNumber());
            event.put("reason", order.getCancellationReason());
            event.put("cancelledAt", order.getCancelledAt());
            
            kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, order.getId().toString(), event);
            log.info("Published ORDER_CANCELLED event for order: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to publish order cancelled event", e);
        }
    }
}








