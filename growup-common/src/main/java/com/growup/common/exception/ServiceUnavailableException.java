package com.growup.common.exception;

/**
 * Exception thrown when a dependent microservice is unavailable.
 * Used by Circuit Breaker fallbacks to indicate service unavailability.
 */
public class ServiceUnavailableException extends RuntimeException {
    
    public ServiceUnavailableException(String message) {
        super(message);
    }
    
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
