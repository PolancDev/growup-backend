package com.growup.backend.infrastructure.exception;

import com.growup.backend.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Controlador global de excepciones.
 * Centraliza la gestión de errores para devolver siempre el formato
 * ErrorResponse definido en OpenAPI.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        log.error("GrowUp-Log: GlobalExceptionHandler - Acceso denegado: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción");
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Credenciales inválidas: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Error de autenticación: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Error de autenticación: " + ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Recurso no encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleConcurrencyConflict(OptimisticLockingFailureException ex) {
        log.warn(
                "GrowUp-Log: GlobalExceptionHandler - Conflicto de concurrencia: Los datos han sido modificados por otro usuario");
        return buildResponse(HttpStatus.CONFLICT,
                "Los datos han sido modificados por otro usuario. Por favor, refresca la página.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("GrowUp-Log: GlobalExceptionHandler - Error de validación: {}", details);
        return buildResponse(HttpStatus.BAD_REQUEST, "Errores de validación: " + details);
    }

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        log.warn("GrowUp-Log: GlobalExceptionHandler - Petición inválida o estado incorrecto: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("GrowUp-Log: GlobalExceptionHandler - Error interno no controlado", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado en el servidor");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse()
                .status(status.value())
                .message(message)
                .timestamp(OffsetDateTime.now());

        return new ResponseEntity<>(error, status);
    }

}
