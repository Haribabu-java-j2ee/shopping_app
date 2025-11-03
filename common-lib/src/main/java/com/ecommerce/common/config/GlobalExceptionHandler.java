package com.ecommerce.common.config;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.dto.ErrorDetails;
import com.ecommerce.common.exception.BaseException;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for all REST controllers.
 * 
 * <p>Implements centralized exception handling using Spring's @RestControllerAdvice.
 * This follows the Aspect-Oriented Programming (AOP) paradigm and provides:
 * <ul>
 *   <li>Consistent error response format across all services</li>
 *   <li>Proper HTTP status code mapping</li>
 *   <li>Detailed error information for debugging</li>
 *   <li>Field-level validation error details</li>
 * </ul>
 * 
 * <p><b>Design Pattern:</b> Chain of Responsibility - Each exception handler 
 * is tried in sequence until a matching handler is found.
 * 
 * @author E-Commerce Platform Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles custom application exceptions that extend BaseException.
     * 
     * @param ex The BaseException thrown
     * @param request Web request context
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(
            BaseException ex, WebRequest request) {
        
        log.error("BaseException occurred: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getHttpStatusCode()));
    }
    
    /**
     * Handles ResourceNotFoundException specifically.
     * Returns HTTP 404 Not Found.
     * 
     * @param ex The ResourceNotFoundException thrown
     * @param request Web request context
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles BusinessException for business logic violations.
     * Returns HTTP 400 Bad Request.
     * 
     * @param ex The BusinessException thrown
     * @param request Web request context
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.error("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles UnauthorizedException for authentication failures.
     * Returns HTTP 401 Unauthorized.
     * 
     * @param ex The UnauthorizedException thrown
     * @param request Web request context
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        log.error("Unauthorized access attempt: {}", ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles validation errors from @Valid annotations.
     * Returns HTTP 400 Bad Request with detailed field errors.
     * 
     * <p>This handler provides field-level validation details, making it easy
     * for clients to identify and fix validation issues.
     * 
     * @param ex The MethodArgumentNotValidException thrown
     * @param request Web request context
     * @return ResponseEntity with detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.error("Validation error occurred: {}", ex.getMessage());
        
        List<ErrorDetails.FieldError> fieldErrors = new ArrayList<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(ErrorDetails.FieldError.builder()
                    .field(error.getField())
                    .rejectedValue(error.getRejectedValue())
                    .message(error.getDefaultMessage())
                    .build());
        }
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("VALIDATION_ERROR")
                .errorMessage("Input validation failed")
                .fieldErrors(fieldErrors)
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(
                "Validation failed for input parameters", errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles all other uncaught exceptions.
     * Returns HTTP 500 Internal Server Error.
     * 
     * <p>This is the catch-all handler for unexpected errors.
     * In production, stack traces should be logged but not exposed to clients.
     * 
     * @param ex The Exception thrown
     * @param request Web request context
     * @return ResponseEntity with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage("An unexpected error occurred. Please try again later.")
                // Include stack trace only in development/debug mode
                // .stackTrace(ex.getStackTrace())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(
                "Internal server error", errorDetails);
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


