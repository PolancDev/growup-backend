package com.growup.course.infrastructure.adapter.persistence;

import com.growup.common.exception.ServiceUnavailableException;
import com.growup.course.domain.model.InstructorInfo;
import com.growup.course.domain.port.out.InstructorLookupPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.UUID;

/**
 * Adaptador para consultar información del instructor desde Auth Service.
 * Comunicación vía REST con el microservicio de autenticación.
 * Implementa Circuit Breaker para tolerancia a fallos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InstructorLookupAdapter implements InstructorLookupPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    private static final String CIRCUIT_BREAKER_NAME = "authService";
    private static final String RETRY_NAME = "authServiceRetry";

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "findInstructorFallback")
    @Retry(name = RETRY_NAME)
    public InstructorInfo findInstructorInfoById(UUID instructorId) {
        if (instructorId == null) {
            return null;
        }

        try {
            log.debug("Consultando instructor {} en Auth Service: {}", instructorId, authServiceUrl);

            // Call to Auth Service: GET /api/v1/admin/users/{id}
            InstructorInfoResponse response = webClientBuilder.build()
                    .get()
                    .uri(authServiceUrl + "/api/v1/admin/users/" + instructorId)
                    .retrieve()
                    .bodyToMono(InstructorInfoResponse.class)
                    .block();

            if (response != null) {
                log.debug("Instructor encontrado: {}", response.name());
                return InstructorInfo.builder()
                        .id(response.id())
                        .name(response.name())
                        .bio(response.bio())
                        .avatarUrl(response.avatarUrl() != null ? URI.create(response.avatarUrl()) : null)
                        .build();
            }

            log.warn("No se encontró instructor con ID: {}", instructorId);
            return null;

        } catch (Exception e) {
            log.error("Error al consultar instructor {}: {}", instructorId, e.getMessage());
            throw new ServiceUnavailableException("Auth Service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method when Circuit Breaker is OPEN or service unavailable.
     * Returns minimal instructor data to allow the operation to continue gracefully.
     *
     * @param instructorId the instructor ID
     * @param throwable the exception that triggered the fallback
     * @return InstructorInfo with minimal/default data
     */
    public InstructorInfo findInstructorFallback(UUID instructorId, Throwable throwable) {
        log.warn("Circuit Breaker fallback for instructor {}. Reason: {}", instructorId, throwable.getMessage());
        
        // Return minimal data to allow the operation to continue gracefully
        return InstructorInfo.builder()
                .id(instructorId)
                .name("Instructor " + instructorId)
                .bio("Información temporalmente no disponible")
                .build();
    }

    /**
     * Async version with TimeLimiter for reactive scenarios
     */
    @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
    public java.util.concurrent.CompletableFuture<InstructorInfo> findInstructorInfoAsync(UUID instructorId) {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> findInstructorInfoById(instructorId));
    }

    /**
     * DTO for Auth Service user response
     */
    private record InstructorInfoResponse(UUID id, String name, String bio, String avatarUrl) {}
}
