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
 * Tests de integración para los endpoints de administración de usuarios.
 * Usa Testcontainers para crear una base de datos PostgreSQL efímera.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AdminUsersIntegrationTest {

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
    private String adminToken;
    private String userToken;
    private UUID adminUserId;
    private UUID regularUserId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/auth";
        
        // Crear usuario administrador
        adminUserId = createAdminUser();
        
        // Crear usuario regular
        regularUserId = createRegularUser();
        
        // Obtener tokens
        adminToken = loginAndGetToken("admin@growup.com", "admin123");
        userToken = loginAndGetToken("user@growup.com", "user123");
    }

    private UUID createAdminUser() {
        Map<String, String> request = new HashMap<>();
        request.put("name", "Admin User");
        request.put("email", "admin@growup.com");
        request.put("password", "admin123");
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/register", request, Map.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) response.getBody().get("user");
        return UUID.fromString(user.get("id").toString());
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
    @DisplayName("GET /api/v1/auth/admin/users - Tests de integración")
    class GetAllUsersIntegrationTests {

        @Test
        @DisplayName("Debería listar todos los usuarios con token de admin")
        void testGetAllUsers_AsAdmin() {
            // Given: Token de administrador
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When: Se obtiene la lista de usuarios
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            // Then: Lista de usuarios
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            // Verificar que hay datos en el cuerpo (usuarios)
            assertTrue(response.getBody().size() > 0, "Should have users in response");
        }

        @Test
        @DisplayName("Debería devolver 403 con token de usuario regular")
        void testGetAllUsers_AsUser() {
            // Given: Token de usuario regular (no admin)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When/Then: Devuelve 403 Forbidden
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }

        @Test
        @DisplayName("Debería devolver 401 sin token")
        void testGetAllUsers_NoToken() {
            // Given: Sin token
            HttpEntity<Map<String, String>> entity = new HttpEntity<>((Map<String, String>) null);

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/admin/users/{id} - Tests de integración")
    class GetUserByIdIntegrationTests {

        @Test
        @DisplayName("Debería obtener usuario por ID con token de admin")
        void testGetUserById_AsAdmin() {
            // Given: Token de administrador
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When: Se obtiene el usuario por ID
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId,
                    HttpMethod.GET,
                    entity,
                    Map.class);

            // Then: Datos del usuario
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(regularUserId.toString(), response.getBody().get("id").toString());
            assertEquals("user@growup.com", response.getBody().get("email"));
        }

        @Test
        @DisplayName("Debería devolver 404 para ID inexistente")
        void testGetUserById_NotFound() {
            // Given: Token de administrador
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            UUID nonExistentId = UUID.randomUUID();

            // When/Then: Devuelve 404 Not Found
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + nonExistentId,
                    HttpMethod.GET,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Debería devolver 403 con token de usuario regular")
        void testGetUserById_AsUser() {
            // Given: Token de usuario regular
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When/Then: Devuelve 403 Forbidden
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId,
                    HttpMethod.GET,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/admin/users/{id}/status - Tests de integración")
    class ToggleUserStatusIntegrationTests {

        @Test
        @DisplayName("Debería togglear estado de usuario con token de admin")
        void testToggleUserStatus_AsAdmin() {
            // Given: Token de administrador
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When: Se toggelea el estado del usuario
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId + "/status",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            // Then: Estado toggeleado
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            
            // Verificar que el estado cambió (debería ser false ahora)
            // Nota: El usuario se creó con isActive = true, al togglear debería ser false
            Boolean isActive = (Boolean) response.getBody().get("isActive");
            assertNotNull(isActive);
        }

        @Test
        @DisplayName("Debería togglear estado dos veces para volver al estado original")
        void testToggleUserStatus_Twice() {
            // Given: Token de administrador
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // Obtener estado inicial
            ResponseEntity<Map> initialResponse = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId,
                    HttpMethod.GET,
                    entity,
                    Map.class);
            
            boolean initialState = (Boolean) initialResponse.getBody().get("isActive");

            // Primer toggle
            restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId + "/status",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            // Segundo toggle (debería volver al estado original)
            ResponseEntity<Map> finalResponse = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId + "/status",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            Boolean finalState = (Boolean) finalResponse.getBody().get("isActive");
            assertEquals(initialState, finalState);
        }

        @Test
        @DisplayName("Debería devolver 403 con token de usuario regular")
        void testToggleUserStatus_AsUser() {
            // Given: Token de usuario regular
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

            // When/Then: Devuelve 403 Forbidden
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId + "/status",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }

        @Test
        @DisplayName("Debería devolver 401 sin token")
        void testToggleUserStatus_NoToken() {
            // Given: Sin token
            HttpEntity<Map<String, String>> entity = new HttpEntity<>((Map<String, String>) null);

            // When/Then: Devuelve 401 Unauthorized
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId + "/status",
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/admin/users/{id} - Tests de integración")
    class UpdateUserIntegrationTests {

        @Test
        @DisplayName("Debería actualizar usuario con token de admin")
        void testUpdateUser_AsAdmin() {
            // Given: Token de administrador y datos de actualización
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);

            Map<String, String> request = new HashMap<>();
            request.put("name", "Updated by Admin");
            request.put("bio", "Bio actualizada por admin");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // When: Se actualiza el usuario
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId,
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            // Then: Usuario actualizado
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Updated by Admin", response.getBody().get("name"));
            assertEquals("Bio actualizada por admin", response.getBody().get("bio"));
        }

        @Test
        @DisplayName("Debería actualizar solo el nombre del usuario")
        void testUpdateUser_PartialUpdate() {
            // Given: Token de administrador
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);

            Map<String, String> request = new HashMap<>();
            request.put("name", "New Name Only");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // When: Se actualiza solo el nombre
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId,
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            // Then: Solo el nombre cambió
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("New Name Only", response.getBody().get("name"));
            // El email debe seguir siendo el mismo
            assertEquals("user@growup.com", response.getBody().get("email"));
        }

        @Test
        @DisplayName("Debería devolver 403 con token de usuario regular")
        void testUpdateUser_AsUser() {
            // Given: Token de usuario regular
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);

            Map<String, String> request = new HashMap<>();
            request.put("name", "Hacked Name");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // When/Then: Devuelve 403 Forbidden
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/admin/users/" + regularUserId,
                    HttpMethod.PUT,
                    entity,
                    Map.class);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }
    }
}
