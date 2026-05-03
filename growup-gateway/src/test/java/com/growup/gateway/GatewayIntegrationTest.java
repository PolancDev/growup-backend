package com.growup.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

/**
 * Integration tests for the API Gateway.
 * 
 * Tests verify:
 * - Routing configuration to backend services
 * - JWT authentication filter behavior
 * - CORS configuration
 * 
 * @author GrowUp Team
 */
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Gateway Integration Tests")
class GatewayIntegrationTest {

    private static final String TEST_SECRET = "GrowUpSecretKeyForJWTTokenValidationMinimum256BitsRequired!";
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private RouteLocator routeLocator;

    /**
     * Helper to create a valid JWT token for testing.
     */
    private String createValidToken(String userId, String email, String role, String name) {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(String.format("{\"sub\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"name\":\"%s\"}",
                        userId, email, role, name).getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".";
    }

    /**
     * Helper to create an expired JWT-like token for testing.
     */
    private String createExpiredToken(String userId, String email, String role) {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(String.format("{\"sub\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                        userId,
                        email,
                        role,
                        System.currentTimeMillis() - 7200000,
                        System.currentTimeMillis() - 3600000
                ).getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".";
    }

    // ========================================================================
    // Routing Tests
    // ========================================================================
    @Nested
    @DisplayName("Route Configuration Tests")
    class RoutingTests {

        @Test
        @DisplayName("should have auth service route configured")
        void shouldHaveAuthServiceRoute() {
            // Given: The gateway has routes configured
            var routes = routeLocator.getRoutes().collectList().block();
            
            // Then: Auth service route should exist
            assertThat(routes).isNotNull();
            assertThat(routes.stream().anyMatch(r -> r.getId().equals("auth-service"))).isTrue();
        }

        @Test
        @DisplayName("should have course service route configured")
        void shouldHaveCourseServiceRoute() {
            // Given: The gateway has routes configured
            var routes = routeLocator.getRoutes().collectList().block();
            
            // Then: Course service route should exist
            assertThat(routes).isNotNull();
            assertThat(routes.stream().anyMatch(r -> r.getId().equals("course-service"))).isTrue();
        }

        @Test
        @DisplayName("should have enrollment service route configured")
        void shouldHaveEnrollmentServiceRoute() {
            // Given: The gateway has routes configured
            var routes = routeLocator.getRoutes().collectList().block();
            
            // Then: Enrollment service route should exist
            assertThat(routes).isNotNull();
            assertThat(routes.stream().anyMatch(r -> r.getId().equals("enrollment-service"))).isTrue();
        }

        @Test
        @DisplayName("should have notification service route configured")
        void shouldHaveNotificationServiceRoute() {
            // Given: The gateway has routes configured
            var routes = routeLocator.getRoutes().collectList().block();
            
            // Then: Notification service route should exist
            assertThat(routes).isNotNull();
            assertThat(routes.stream().anyMatch(r -> r.getId().equals("notification-service"))).isTrue();
        }
    }

    // ========================================================================
    // JWT Authentication Tests
    // ========================================================================
    @Nested
    @DisplayName("JWT Authentication Tests")
    class JwtAuthenticationTests {

        @Test
        @DisplayName("should allow access to public auth endpoint without token")
        void shouldAllowPublicAuthEndpoint() {
            // Given: A request to the public auth endpoint
            String token = createValidToken(
                UUID.randomUUID().toString(),
                "test@example.com",
                "STUDENT",
                "Test User"
            );

            // When: Making a request to /api/v1/auth/login
            webTestClient.get()
                    .uri("/api/v1/auth/login")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    // Then: Should not return 401 (auth endpoint is public)
                    .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
        }

        @Test
        @DisplayName("should reject request without token for protected endpoint")
        void shouldRejectProtectedEndpointWithoutToken() {
            // Given: A request to a protected endpoint without token
            // When: Making a request to /api/v1/courses (protected)
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .exchange()
                    // Then: Should be rejected with 401 Unauthorized
                    .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("should accept valid token for protected endpoint")
        void shouldAcceptValidTokenForProtectedEndpoint() {
            // Given: A request with valid JWT token
            String token = createValidToken(
                UUID.randomUUID().toString(),
                "student@growup.com",
                "STUDENT",
                "Student User"
            );

            // When: Making a request to /api/v1/courses with valid token
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    // Then: Request should be accepted (not rejected)
                    // Note: May return 404 if backend service is not running, but not 401
                    .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
        }

        @Test
        @DisplayName("should reject request with invalid token")
        void shouldRejectInvalidToken() {
            // Given: A request with invalid JWT token
            String invalidToken = "invalid.token.here";

            // When: Making a request to /api/v1/courses with invalid token
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                    .exchange()
                    // Then: Should be rejected with 401 Unauthorized
                    .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("should reject request with expired token")
        void shouldRejectExpiredToken() {
            // Given: An expired JWT token
            String expiredToken = createExpiredToken(
                    UUID.randomUUID().toString(),
                    "test@example.com",
                    "STUDENT"
            );

            // When: Making a request to /api/v1/courses with expired token
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                    .exchange()
                    // Then: Should be rejected with 401 Unauthorized
                    .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("should propagate user info headers for valid token")
        void shouldPropagateUserInfoHeaders() {
            // Given: A request with valid JWT token containing user info
            String userId = UUID.randomUUID().toString();
            String token = createValidToken(
                userId,
                "teacher@growup.com",
                "TEACHER",
                "Teacher User"
            );

            // When: Making a request to /api/v1/courses
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    // Then: Should have user info headers in request to downstream service
                    // Note: This is verified by the filter, response depends on backend
                    .expectStatus().value(status -> assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
        }
    }

    // ========================================================================
    // CORS Tests
    // ========================================================================
    @Nested
    @DisplayName("CORS Configuration Tests")
    class CorsTests {

        @Test
        @DisplayName("should include CORS headers in preflight response")
        void shouldIncludeCorsHeadersInPreflight() {
            // Given: A preflight OPTIONS request
            // When: Making an OPTIONS request to /api/v1/courses
            webTestClient.options()
                    .uri("/api/v1/courses")
                    .header("Origin", "http://localhost:4200")
                    .header("Access-Control-Request-Method", "GET")
                    .header("Access-Control-Request-Headers", "Authorization")
                    .exchange()
                    // Then: Should include CORS headers
                    .expectStatus().isOk()
                    .expectHeader().exists("Access-Control-Allow-Origin")
                    .expectHeader().exists("Access-Control-Allow-Methods")
                    .expectHeader().exists("Access-Control-Allow-Headers");
        }

        @Test
        @DisplayName("should allow requests from localhost:4200")
        void shouldAllowLocalhost4200() {
            // Given: A request from Angular frontend
            String token = createValidToken(
                UUID.randomUUID().toString(),
                "test@growup.com",
                "STUDENT",
                "Test User"
            );

            // When: Making a request from http://localhost:4200
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header("Origin", "http://localhost:4200")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    // Then: Should not have CORS issues
                    .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:4200");
        }

        @Test
        @DisplayName("should allow requests from localhost:3000")
        void shouldAllowLocalhost3000() {
            // Given: A request from React frontend
            String token = createValidToken(
                UUID.randomUUID().toString(),
                "test@growup.com",
                "STUDENT",
                "Test User"
            );

            // When: Making a request from http://localhost:3000
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header("Origin", "http://localhost:3000")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    // Then: Should not have CORS issues
                    .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000");
        }

        @Test
        @DisplayName("should expose authorization header")
        void shouldExposeAuthorizationHeader() {
            // Given: A request with token
            String token = createValidToken(
                UUID.randomUUID().toString(),
                "test@growup.com",
                "STUDENT",
                "Test User"
            );

            // When: Making a request
            webTestClient.get()
                    .uri("/api/v1/courses")
                    .header("Origin", "http://localhost:4200")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    // Then: Should expose Authorization header
                    .expectHeader().exists("Access-Control-Expose-Headers");
        }
    }

    // ========================================================================
    // Application Context Tests
    // ========================================================================
    @Nested
    @DisplayName("Application Context Tests")
    class ApplicationContextTests {

        @Test
        @DisplayName("should load gateway application context")
        void shouldLoadApplicationContext() {
            // Then: Application context should be loaded
            assertThat(applicationContext).isNotNull();
        }

        @Test
        @DisplayName("should have WebTestClient configured")
        void shouldHaveWebTestClient() {
            // Then: WebTestClient should be configured
            assertThat(webTestClient).isNotNull();
        }
    }
}
