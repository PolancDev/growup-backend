package com.growup.auth.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para los endpoints de autenticación.
 * Usa Testcontainers para crear una base de datos PostgreSQL efímera.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("growup_auth_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String userToken;
    private UUID regularUserId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/auth";
        
        // Crear usuario regular
        regularUserId = createRegularUser();
        
        // Obtener token
        userToken = loginAndGetToken("user@growup.com", "user123");
    }

    private UUID createRegularUser() {
        Map<String, String> request = new HashMap<>();
        request.put("name", "Regular User");
        request.put("email", "user@growup.com");
        request.put("password", "user123");
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/register", request, Map.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) response.getBody().get("user");
        return UUID.fromString(user.get("id").toString());
    }

    private String loginAndGetToken(String email, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/login", request, Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody().get("token").toString();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login - Tests de integración")
    class LoginIntegrationTests {

        @Test
        @DisplayName("Debería hacer login exitosamente con credenciales válidas")
        void testLogin_Success() {
            // Given: Credenciales válidas
            Map<String, String> request = new HashMap<>();
            request.put("email", "user@growup.com");
            request.put("password", "user123");

            // When: Se hace login
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/login", request, Map.class);

            // Then: Respuesta exitosa con token
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().get("token"));
            assertNotNull(response.getBody().get("user"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) response.getBody().get("user");
            assertEquals("user@growup.com", user.get("email"));
            assertEquals("Regular User", user.get("name"));
        }

        @Test
        @DisplayName("Debería devolver 401 con credenciales incorrectas")
        void testLogin_InvalidCredentials() {
            // Given: Credenciales inválidas
            Map<String, String> request = new HashMap<>();
            request.put("email", "user@growup.com");
            request.put("password", "wrongpassword");

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/login", request, Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        @DisplayName("Debería devolver 401 con email inexistente")
        void testLogin_NonExistentUser() {
            // Given: Email que no existe
            Map<String, String> request = new HashMap<>();
            request.put("email", "nonexistent@growup.com");
            request.put("password", "password123");

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/login", request, Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register - Tests de integración")
    class RegisterIntegrationTests {

        @Test
        @DisplayName("Debería registrar usuario exitosamente")
        void testRegister_Success() {
            // Given: Datos de registro válidos
            Map<String, String> request = new HashMap<>();
            request.put("name", "New User");
            request.put("email", "newuser@test.com");
            request.put("password", "newpassword123");
            request.put("bio", "Nuevo usuario de prueba");

            // When: Se registra el usuario
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/register", request, Map.class);

            // Then: Usuario creado con token
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().get("token"));
            assertNotNull(response.getBody().get("user"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) response.getBody().get("user");
            assertEquals("newuser@test.com", user.get("email"));
            assertEquals("New User", user.get("name"));
            assertEquals("Nuevo usuario de prueba", user.get("bio"));
            assertEquals(true, user.get("isActive"));
        }

        @Test
        @DisplayName("Debería devolver error 400 con email duplicado")
        void testRegister_DuplicateEmail() {
            // Given: Email que ya existe
            Map<String, String> request = new HashMap<>();
            request.put("name", "Duplicate User");
            request.put("email", "user@growup.com");
            request.put("password", "password123");

            // When/Then: Devuelve 400 Bad Request
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/register", request, Map.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/me - Tests de integración")
    class MeIntegrationTests {

        @Test
        @DisplayName("Debería obtener perfil del usuario actual autenticado")
        void testGetCurrentUser_Success() {
            // Given: Token de autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When: Se obtiene el perfil
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/me",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            // Then: Datos del usuario actual
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("user@growup.com", response.getBody().get("email"));
            assertEquals("Regular User", response.getBody().get("name"));
        }

        @Test
        @DisplayName("Debería devolver 401 sin token de autenticación")
        void testGetCurrentUser_NoToken() {
            // Given: Sin token
            HttpEntity<Map<String, String>> entity = new HttpEntity<>((Map<String, String>) null);

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/me",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        @DisplayName("Debería devolver 401 con token inválido")
        void testGetCurrentUser_InvalidToken() {
            // Given: Token inválido
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer invalid-token");
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/me",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/me - Tests de integración")
    class UpdateProfileIntegrationTests {

        @Test
        @DisplayName("Debería actualizar perfil exitosamente")
        void testUpdateProfile_Success() {
            // Given: Token y datos de actualización
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            
            Map<String, String> request = new HashMap<>();
            request.put("name", "Updated Name");
            request.put("bio", "Updated bio");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // When: Se actualiza el perfil
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/me",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            // Then: Perfil actualizado
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Updated Name", response.getBody().get("name"));
            assertEquals("Updated bio", response.getBody().get("bio"));
        }

        @Test
        @DisplayName("Debería actualizar contraseña exitosamente")
        void testUpdateProfile_ChangePassword() {
            // Given: Token y nueva contraseña
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            
            Map<String, String> request = new HashMap<>();
            request.put("password", "newpassword456");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // When: Se actualiza la contraseña
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/me",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            // Then: Perfil actualizado
            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Verificar que puede hacer login con la nueva contraseña
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("email", "user@growup.com");
            loginRequest.put("password", "newpassword456");

            ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                    baseUrl + "/login", loginRequest, Map.class);

            assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        }

        @Test
        @DisplayName("Debería devolver 401 al actualizar sin autenticación")
        void testUpdateProfile_NoToken() {
            // Given: Sin token
            Map<String, String> request = new HashMap<>();
            request.put("name", "Updated Name");
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request);

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/me",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }
}
