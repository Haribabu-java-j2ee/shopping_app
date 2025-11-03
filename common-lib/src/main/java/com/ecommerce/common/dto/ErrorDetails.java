package com.ecommerce.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Detailed error information for failed operations.
 * 
 * <p>Provides structured error data for debugging and client handling.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    
    /**
     * Unique error code for programmatic handling.
     */
    private String errorCode;
    
    /**
     * Human-readable error message.
     */
    private String errorMessage;
    
    /**
     * List of field-level validation errors.
     */
    private List<FieldError> fieldErrors;
    
    /**
     * Stack trace for debugging (only in non-production environments).
     */
    private String stackTrace;
    
    /**
     * Represents a field-level validation error.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        /**
         * Name of the field that failed validation.
         */
        private String field;
        
        /**
         * The invalid value provided.
         */
        private Object rejectedValue;
        
        /**
         * Description of why the validation failed.
         */
        private String message;
    }
}


