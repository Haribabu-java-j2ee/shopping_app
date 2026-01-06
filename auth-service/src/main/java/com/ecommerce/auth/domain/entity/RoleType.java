package com.ecommerce.auth.domain.entity;

/**
 * Enum representing different role types in the system.
 */
public enum RoleType {
    /**
     * Administrator with full system access.
     */
    ROLE_ADMIN,
    
    /**
     * Regular customer user.
     */
    ROLE_USER,
    
    /**
     * Customer support representative.
     */
    ROLE_SUPPORT,
    
    /**
     * Merchant or seller.
     */
    ROLE_MERCHANT,
    
    /**
     * Guest user with limited access.
     */
    ROLE_GUEST
}






