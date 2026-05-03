package com.growup.auth.infrastructure.adapter.web;

import com.growup.auth.application.service.AuthService;
import com.growup.auth.domain.model.User;
import com.growup.common.domain.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthenticationWebAdapter.
 * Adaptado para OAuth2/Keycloak con JWT.
 * Patrón Given-When-Then.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationWebAdapterTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthenticationWebAdapter authenticationWebAdapter;

    private UUID userId;
    private User sampleUser;
    private Jwt mockJwt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .name("Pedro Martínez")
                .email("pedro@ejemplo.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .isActive(true)
                .bio("Estudiante de programación")
                .avatar("https://example.com/pedro.jpg")
                .joinDate(OffsetDateTime.now())
                .version(0L)
                .build();

        // Mock de JWT (simula token de Keycloak)
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId.toString());
        claims.put("email", "pedro@ejemplo.com");
        claims.put("name", "Pedro Martínez");
        
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("STUDENT"));
        claims.put("realm_access", realmAccess);

        mockJwt = Jwt.withTokenValue("mock.jwt.token")
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .build();
    }

    @Nested
    @DisplayName("Pruebas para endpoint GET /me (getCurrentUser)")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Debería devolver perfil del usuario actual cuando JWT es válido")
        void testGetCurrentUser_Success() {
            // Given: Usuario autenticado con JWT válido
            when(authService.getUserById(any(UUID.class)))
                    .thenReturn(java.util.Optional.of(sampleUser));

            // When: Se llama al endpoint con el JWT mockeado
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.getCurrentUser(mockJwt);

            // Then: Respuesta con datos del usuario
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(userId.toString(), response.getBody().get("id"));
            assertEquals("pedro@ejemplo.com", response.getBody().get("email"));
            assertEquals("Pedro Martínez", response.getBody().get("name"));
            assertEquals("STUDENT", response.getBody().get("role"));
            verify(authService).getUserById(userId);
        }

        @Test
        @DisplayName("Debería devolver perfil sin bio/avatar si usuario no está en BD")
        void testGetCurrentUser_UserNotInDB() {
            // Given: JWT válido pero usuario no en base de datos
            when(authService.getUserById(any(UUID.class)))
                    .thenReturn(java.util.Optional.empty());

            // When: Se llama al endpoint
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.getCurrentUser(mockJwt);

            // Then: Respuesta con datos del token pero sin campos de BD
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(userId.toString(), response.getBody().get("id"));
            assertNull(response.getBody().get("bio"));
            assertNull(response.getBody().get("avatar"));
        }

        @Test
        @DisplayName("Debería devolver 401 cuando JWT es nulo")
        void testGetCurrentUser_NullJwt() {
            // When: Se llama al endpoint sin JWT
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.getCurrentUser(null);

            // Then: Unauthorized
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Pruebas para endpoint PUT /me (updateProfile)")
    class UpdateProfileTests {

        @Test
        @DisplayName("Debería actualizar perfil correctamente")
        void testUpdateProfile_Success() {
            // Given: Datos de actualización
            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("name", "Pedro Actualizado");
            updateRequest.put("bio", "Nueva biografía");
            updateRequest.put("avatar", "https://example.com/new-avatar.jpg");

            User updatedUser = User.builder()
                    .id(userId)
                    .name("Pedro Actualizado")
                    .email("pedro@ejemplo.com")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .bio("Nueva biografía")
                    .avatar("https://example.com/new-avatar.jpg")
                    .version(1L)
                    .build();

            when(authService.updateProfile(eq(userId.toString()), any(), any(), any(), any(), any()))
                    .thenReturn(updatedUser);

            // When: Se llama al endpoint con JWT y request
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.updateProfile(mockJwt, updateRequest);

            // Then: Respuesta con usuario actualizado
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Pedro Actualizado", response.getBody().get("name"));
            assertEquals("Nueva biografía", response.getBody().get("bio"));
            verify(authService).updateProfile(eq(userId.toString()), eq("Pedro Actualizado"), isNull(), isNull(), eq("https://example.com/new-avatar.jpg"), eq("Nueva biografía"));
        }

        @Test
        @DisplayName("Debería devolver 401 cuando JWT es nulo")
        void testUpdateProfile_NullJwt() {
            // Given
            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("name", "Test");

            // When: Se llama al endpoint sin JWT
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.updateProfile(null, updateRequest);

            // Then: Unauthorized
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }
}
