package com.ecommerce.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * <p>Returns HTTP 404 status code.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
public class ResourceNotFoundException extends BaseException {
    
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
    private static final int HTTP_STATUS = 404;
    
    /**
     * Constructs a ResourceNotFoundException with a message.
     * 
     * @param message Error message
     */
    public ResourceNotFoundException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }
    
    /**
     * Constructs a ResourceNotFoundException with resource type and ID.
     * 
     * @param resourceType Type of resource (e.g., "User", "Order")
     * @param resourceId Resource identifier
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s not found with id: %s", resourceType, resourceId), 
              ERROR_CODE, HTTP_STATUS);
    }
}


