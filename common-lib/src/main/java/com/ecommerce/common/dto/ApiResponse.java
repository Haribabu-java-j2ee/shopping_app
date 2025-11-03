package com.ecommerce.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for all REST endpoints.
 * Provides consistent response structure across all microservices.
 * 
 * <p>This class implements the Builder Pattern for flexible object construction.
 * 
 * @param <T> The type of data being returned in the response
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
public class ApiResponse<T> {
    
    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;
    
    /**
     * Human-readable message describing the result.
     */
    private String message;
    
    /**
     * The actual data payload. Can be any type.
     */
    private T data;
    
    /**
     * Error details if the operation failed.
     */
    private ErrorDetails error;
    
    /**
     * Timestamp when the response was generated.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Creates a successful response with data.
     * 
     * @param <T> The type of data
     * @param message Success message
     * @param data Response data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a successful response without data.
     * 
     * @param <T> The type of data
     * @param message Success message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }
    
    /**
     * Creates an error response.
     * 
     * @param <T> The type of data
     * @param message Error message
     * @param error Error details
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, ErrorDetails error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response with simple message.
     * 
     * @param <T> The type of data
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, ErrorDetails.builder()
                .errorCode("GENERAL_ERROR")
                .errorMessage(message)
                .build());
    }
}


