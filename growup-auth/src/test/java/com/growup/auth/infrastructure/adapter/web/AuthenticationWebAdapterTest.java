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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthenticationWebAdapter.
 * Patrón Given-When-Then.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationWebAdapterTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthenticationWebAdapter authenticationWebAdapter;

    private UUID userId;
    private User sampleUser;

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
    }

    @Nested
    @DisplayName("Pruebas para endpoint de login (/login)")
    class LoginTests {

        @Test
        @DisplayName("Debería hacer login correctamente cuando credenciales son válidas")
        void testLogin_Success() {
            // Given: Credenciales válidas
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("email", "pedro@ejemplo.com");
            loginRequest.put("password", "password123");

            when(authService.login("pedro@ejemplo.com", "password123"))
                    .thenReturn(sampleUser);
            when(authService.generateToken(sampleUser))
                    .thenReturn("jwt.token.string");

            // When: Se llama al endpoint
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.login(loginRequest);

            // Then: Respuesta exitosa
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().get("user"));
            assertNotNull(response.getBody().get("token"));
            verify(authService).login("pedro@ejemplo.com", "password123");
            verify(authService).generateToken(sampleUser);
        }

        @Test
        @DisplayName("Debería devolver error 401 cuando credenciales inválidas")
        void testLogin_InvalidCredentials() {
            // Given: Credenciales inválidas
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("email", "pedro@ejemplo.com");
            loginRequest.put("password", "wrongPassword");

            when(authService.login("pedro@ejemplo.com", "wrongPassword"))
                    .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas"));

            // When/Then: Lanza excepción
            assertThrows(
                    org.springframework.security.authentication.BadCredentialsException.class,
                    () -> authenticationWebAdapter.login(loginRequest)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para endpoint de registro (/register)")
    class RegisterTests {

        @Test
        @DisplayName("Debería registrar usuario correctamente")
        void testRegister_Success() {
            // Given: Datos de registro válidos
            Map<String, String> registerRequest = new HashMap<>();
            registerRequest.put("name", "Nuevo Usuario");
            registerRequest.put("email", "nuevo@ejemplo.com");
            registerRequest.put("password", "password123");
            registerRequest.put("bio", "Nueva biografía");

            User newUser = User.builder()
                    .id(userId)
                    .name("Nuevo Usuario")
                    .email("nuevo@ejemplo.com")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .bio("Nueva biografía")
                    .joinDate(OffsetDateTime.now())
                    .version(0L)
                    .build();

            when(authService.register(any(User.class), anyString()))
                    .thenReturn(newUser);
            when(authService.generateToken(newUser))
                    .thenReturn("jwt.token.string");

            // When: Se llama al endpoint
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.register(registerRequest);

            // Then: Respuesta creada
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().get("user"));
            assertNotNull(response.getBody().get("token"));
            verify(authService).register(any(User.class), eq("password123"));
        }

        @Test
        @DisplayName("Debería devolver error cuando email ya existe")
        void testRegister_DuplicateEmail() {
            // Given: Email ya existe
            Map<String, String> registerRequest = new HashMap<>();
            registerRequest.put("name", "Usuario Existente");
            registerRequest.put("email", "existente@ejemplo.com");
            registerRequest.put("password", "password123");

            when(authService.register(any(User.class), anyString()))
                    .thenThrow(new IllegalArgumentException("El email ya está registrado"));

            // When/Then: Lanza excepción
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationWebAdapter.register(registerRequest)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para endpoint de perfil actual (/me)")
    class MeTests {

        @Test
        @DisplayName("Debería devolver perfil del usuario actual")
        void testGetCurrentUser_Success() {
            // Given: Usuario autenticado
            setupSecurityContext("pedro@ejemplo.com");

            when(authService.getUser("pedro@ejemplo.com"))
                    .thenReturn(sampleUser);

            // When: Se llama al endpoint
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.getCurrentUser();

            // Then: Respuesta con datos del usuario
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("pedro@ejemplo.com", response.getBody().get("email"));
            verify(authService).getUser("pedro@ejemplo.com");
        }

        private void setupSecurityContext(String email) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, java.util.List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Nested
    @DisplayName("Pruebas para endpoint de actualización de perfil (/me)")
    class UpdateProfileTests {

        @Test
        @DisplayName("Debería actualizar perfil correctamente")
        void testUpdateProfile_Success() {
            // Given: Datos de actualización
            setupSecurityContext("pedro@ejemplo.com");

            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("name", "Pedro Actualizado");
            updateRequest.put("bio", "Nueva biografía");

            User updatedUser = User.builder()
                    .id(userId)
                    .name("Pedro Actualizado")
                    .email("pedro@ejemplo.com")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .bio("Nueva biografía")
                    .version(1L)
                    .build();

            when(authService.updateProfile(anyString(), any(), any(), any(), any(), any()))
                    .thenReturn(updatedUser);

            // When: Se llama al endpoint
            ResponseEntity<Map<String, Object>> response =
                    authenticationWebAdapter.updateProfile(updateRequest);

            // Then: Respuesta con usuario actualizado
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Pedro Actualizado", response.getBody().get("name"));
        }

        private void setupSecurityContext(String email) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, java.util.List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
