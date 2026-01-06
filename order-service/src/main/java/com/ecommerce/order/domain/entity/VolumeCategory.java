package com.ecommerce.order.domain.entity;

/**
 * Enum representing customer volume categories based on total transaction value.
 * 
 * <p>Classification Criteria (based on cumulative transaction value):
 * <ul>
 *   <li>{@link #HIGH_VOLUME} - Total order value greater than 50,000</li>
 *   <li>{@link #MID_VOLUME} - Total order value between 20,000 and 50,000 (inclusive)</li>
 *   <li>{@link #LOW_VOLUME} - Total order value below 20,000</li>
 * </ul>
 * 
 * <p><b>Usage:</b> This enum is used by the Customer Volume Stats API to categorize 
 * customers based on their aggregate transaction value.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 * @see com.ecommerce.order.service.CustomerStatsService
 */
public enum VolumeCategory {
    
    /**
     * High volume category for customers with total spend exceeding 50,000.
     */
    HIGH_VOLUME("High Volume Spend Customer"),
    
    /**
     * Mid volume category for customers with total spend between 20,000 and 50,000.
     */
    MID_VOLUME("Mid Volume Spend Customer"),
    
    /**
     * Low volume category for customers with total spend below 20,000.
     */
    LOW_VOLUME("Low Volume Spend Customer");
    
    /**
     * Human-readable description of the category.
     */
    private final String description;
    
    /**
     * Constructor for VolumeCategory.
     * 
     * @param description Human-readable description
     */
    VolumeCategory(String description) {
        this.description = description;
    }
    
    /**
     * Gets the human-readable description of the volume category.
     * 
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
}

