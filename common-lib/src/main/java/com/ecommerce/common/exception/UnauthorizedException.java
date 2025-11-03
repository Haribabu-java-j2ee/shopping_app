package com.ecommerce.common.exception;

/**
 * Exception thrown when authentication fails or is missing.
 * 
 * <p>Returns HTTP 401 status code.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
public class UnauthorizedException extends BaseException {
    
    private static final String ERROR_CODE = "UNAUTHORIZED";
    private static final int HTTP_STATUS = 401;
    
    /**
     * Constructs an UnauthorizedException with a message.
     * 
     * @param message Error message
     */
    public UnauthorizedException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }
    
    /**
     * Constructs an UnauthorizedException with default message.
     */
    public UnauthorizedException() {
        super("Authentication is required to access this resource", ERROR_CODE, HTTP_STATUS);
    }
}


