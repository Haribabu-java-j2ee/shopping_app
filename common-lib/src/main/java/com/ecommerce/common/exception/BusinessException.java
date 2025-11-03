package com.ecommerce.common.exception;

/**
 * Exception thrown for business logic violations.
 * 
 * <p>Returns HTTP 400 status code.
 * Used when business rules are violated (e.g., insufficient inventory, invalid state transitions).
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
public class BusinessException extends BaseException {
    
    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";
    private static final int HTTP_STATUS = 400;
    
    /**
     * Constructs a BusinessException with a message.
     * 
     * @param message Error message describing the business rule violation
     */
    public BusinessException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }
    
    /**
     * Constructs a BusinessException with a message and custom error code.
     * 
     * @param message Error message
     * @param errorCode Custom error code
     */
    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HTTP_STATUS);
    }
    
    /**
     * Constructs a BusinessException with a message and cause.
     * 
     * @param message Error message
     * @param cause The underlying cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
    }
}


