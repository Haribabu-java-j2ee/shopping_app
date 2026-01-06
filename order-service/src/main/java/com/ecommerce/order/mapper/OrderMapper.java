package com.ecommerce.order.mapper;

import com.ecommerce.order.domain.entity.Address;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.dto.AddressDTO;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.dto.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Order entities and DTOs.
 * 
 * <p>Provides automatic mapping between domain models and data transfer objects.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    /**
     * Converts Order entity to OrderDTO.
     * 
     * @param order Order entity
     * @return OrderDTO
     */
    OrderDTO toDTO(Order order);
    
    /**
     * Converts OrderItem entity to OrderItemDTO.
     * 
     * @param orderItem OrderItem entity
     * @return OrderItemDTO
     */
    @Mapping(target = "productId", source = "productId")
    OrderItemDTO toDTO(OrderItem orderItem);
    
    /**
     * Converts Address to AddressDTO.
     * 
     * @param address Address entity
     * @return AddressDTO
     */
    AddressDTO toDTO(Address address);
    
    /**
     * Converts AddressDTO to Address.
     * 
     * @param addressDTO AddressDTO
     * @return Address entity
     */
    Address toEntity(AddressDTO addressDTO);
}








