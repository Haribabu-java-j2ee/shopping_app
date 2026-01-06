package com.ecommerce.order.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Address value object for shipping and billing addresses.
 * 
 * <p>This is an embeddable class that can be used within other entities.
 * It represents a complete address but doesn't have its own identity.
 * 
 * <p><b>Design Pattern:</b> Value Object Pattern - Address is immutable
 * and defined by its attributes, not by identity.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    /**
     * Street address including house/building number.
     */
    @Column(name = "street", length = 200)
    private String street;
    
    /**
     * City name.
     */
    @Column(name = "city", length = 100)
    private String city;
    
    /**
     * State or province.
     */
    @Column(name = "state", length = 100)
    private String state;
    
    /**
     * Postal or ZIP code.
     */
    @Column(name = "zip_code", length = 20)
    private String zipCode;
    
    /**
     * Country name or code.
     */
    @Column(name = "country", length = 100)
    private String country;
    
    /**
     * Returns formatted address string.
     * 
     * @return Complete formatted address
     */
    public String getFormattedAddress() {
        return String.format("%s, %s, %s %s, %s",
                street, city, state, zipCode, country);
    }
}








