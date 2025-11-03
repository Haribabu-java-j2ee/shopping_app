package com.ecommerce.common.exception;

import lombok.Getter;

/**
 * Base exception class for all custom exceptions in the application.
 * 
 * <p>Provides common error code functionality and follows the Exception Hierarchy Pattern.
 * All custom exceptions should extend this class.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public abstract class BaseException extends RuntimeException {
    
    /**
     * Unique error code for this exception.
     */
    private final String errorCode;
    
    /**
     * HTTP status code to be returned.
     */
    private final int httpStatusCode;
    
    /**
     * Constructs a new BaseException with message and error code.
     * 
     * @param message Error message
     * @param errorCode Unique error code
     * @param httpStatusCode HTTP status code
     */
    protected BaseException(String message, String errorCode, int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
    
    /**
     * Constructs a new BaseException with message, error code, and cause.
     * 
     * @param message Error message
     * @param errorCode Unique error code
     * @param httpStatusCode HTTP status code
     * @param cause The underlying cause
     */
    protected BaseException(String message, String errorCode, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
}


