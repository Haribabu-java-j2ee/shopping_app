package com.ecommerce.order.domain.entity;

/**
 * Enum representing customer spend categories based on single transaction amounts.
 * 
 * <p>Classification Criteria (based on individual transaction amount):
 * <ul>
 *   <li>{@link #HIGH_SPEND} - Any single transaction amount greater than 50,000</li>
 *   <li>{@link #MID_SPEND} - Any single transaction between 20,000 and 50,000 (inclusive)</li>
 *   <li>{@link #LOW_SPEND} - All transactions are below 20,000</li>
 * </ul>
 * 
 * <p><b>Usage:</b> This enum is used by the Customer Stats API to categorize 
 * customers based on their highest single transaction value.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see com.ecommerce.order.service.CustomerStatsService
 */
public enum SpendCategory {
    
    /**
     * High spend category for customers with any transaction exceeding 50,000.
     */
    HIGH_SPEND("High Spend User"),
    
    /**
     * Mid spend category for customers with transactions between 20,000 and 50,000.
     */
    MID_SPEND("Mid Spend User"),
    
    /**
     * Low spend category for customers with all transactions below 20,000.
     */
    LOW_SPEND("Low Spend User");
    
    /**
     * Human-readable description of the category.
     */
    private final String description;
    
    /**
     * Constructor for SpendCategory.
     * 
     * @param description Human-readable description
     */
    SpendCategory(String description) {
        this.description = description;
    }
    
    /**
     * Gets the human-readable description of the spend category.
     * 
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
}

