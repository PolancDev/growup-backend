package com.growup.auth.infrastructure.exception;

import com.growup.auth.domain.exception.InvalidCredentialsException;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador global de excepciones.
 * Centraliza la gestión de errores.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        log.error("GrowUp-Log: GlobalExceptionHandler - Acceso denegado: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción");
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Credenciales inválidas: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Credenciales inválidas: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Error de autenticación: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Error de autenticación: " + ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Recurso no encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleConcurrencyConflict(OptimisticLockingFailureException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Conflicto de concurrencia");
        return buildResponse(HttpStatus.CONFLICT, "Los datos han sido modificados por otro usuario. Por favor, refresca la página.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("GrowUp-Log: GlobalExceptionHandler - Error de validación: {}", details);
        return buildResponse(HttpStatus.BAD_REQUEST, "Errores de validación: " + details);
    }

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Petición inválida: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("GrowUp-Log: GlobalExceptionHandler - Error interno no controlado", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado en el servidor");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", status.value());
        error.put("message", message);
        error.put("timestamp", OffsetDateTime.now());

        return new ResponseEntity<>(error, status);
    }
}
