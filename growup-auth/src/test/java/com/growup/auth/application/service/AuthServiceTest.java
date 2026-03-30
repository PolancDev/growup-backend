package com.growup.auth.application.service;

import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.out.TokenGeneratorPort;
import com.growup.auth.domain.port.out.UserPersistencePort;
import com.growup.common.domain.model.enums.Role;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService.
 * Patrón Given-When-Then.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private UUID userId;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .name("María García")
                .email("maria@ejemplo.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .isActive(true)
                .joinDate(OffsetDateTime.now())
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Pruebas para login (login)")
    class LoginTests {

        @Test
        @DisplayName("Debería hacer login correctamente cuando credenciales son válidas")
        void testLogin_Success() {
            // Given: Usuario existe en la base de datos
            when(userPersistencePort.findByEmail("maria@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            // When: Se llama al método login
            User result = authService.login("maria@ejemplo.com", "password123");

            // Then: Devuelve el usuario correctamente
            assertNotNull(result);
            assertEquals("maria@ejemplo.com", result.getEmail());
            assertEquals("María García", result.getName());
            verify(authenticationManager).authenticate(any());
            verify(userPersistencePort).findByEmail("maria@ejemplo.com");
        }

        @Test
        @DisplayName("Debería lanzar excepción BadCredentials cuando la contraseña es inválida")
        void testLogin_InvalidPassword() {
            // Given: Usuario existe pero contraseña inválida
            doThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas"))
                    .when(authenticationManager).authenticate(any());

            // When/Then: Lanza excepción
            assertThrows(
                    org.springframework.security.authentication.BadCredentialsException.class,
                    () -> authService.login("maria@ejemplo.com", "wrongPassword")
            );
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException cuando el usuario no existe")
        void testLogin_UserNotFound() {
            // Given: Usuario no existe en la base de datos
            doAnswer(inv -> null).when(authenticationManager).authenticate(any());
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com"))
                    .thenReturn(Optional.empty());

            // When/Then: Lanza ResourceNotFoundException
            assertThrows(ResourceNotFoundException.class,
                    () -> authService.login("noexiste@ejemplo.com", "password123"));
        }
    }

    @Nested
    @DisplayName("Pruebas para registro (register)")
    class RegisterTests {

        @Test
        @DisplayName("Debería registrar usuario correctamente cuando el email no existe")
        void testRegister_Success() {
            // Given: Email no existe en la base de datos
            when(userPersistencePort.findByEmail("nuevo@ejemplo.com"))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString()))
                    .thenReturn("hashedPassword");
            when(userPersistencePort.save(any(User.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When: Se registra un nuevo usuario
            User newUser = User.builder()
                    .name("Nuevo Usuario")
                    .email("nuevo@ejemplo.com")
                    .role(Role.STUDENT)
                    .build();

            User result = authService.register(newUser, "password123");

            // Then: Usuario registrado correctamente
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals("nuevo@ejemplo.com", result.getEmail());
            assertEquals("Nuevo Usuario", result.getName());
            assertNotNull(result.getJoinDate());
            assertTrue(result.getIsActive());
            verify(passwordEncoder).encode("password123");
            verify(userPersistencePort).save(any(User.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException cuando el email ya existe")
        void testRegister_DuplicateEmail() {
            // Given: Email ya existe
            when(userPersistencePort.findByEmail("existente@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            // When/Then: Lanza IllegalArgumentException
            User existingUser = User.builder()
                    .email("existente@ejemplo.com")
                    .name("Usuario Existente")
                    .build();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.register(existingUser, "password123")
            );

            assertEquals("El email ya está registrado", exception.getMessage());
            verify(userPersistencePort, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para generación de token (generateToken)")
    class GenerateTokenTests {

        @Test
        @DisplayName("Debería generar token correctamente")
        void testGenerateToken_Success() {
            // Given: Token generator retorna un token
            when(tokenGeneratorPort.generateToken(sampleUser))
                    .thenReturn("jwt.token.string");

            // When: Se genera el token
            String result = authService.generateToken(sampleUser);

            // Then: Token generado correctamente
            assertEquals("jwt.token.string", result);
            verify(tokenGeneratorPort).generateToken(sampleUser);
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener usuario por email (getUser)")
    class GetUserTests {

        @Test
        @DisplayName("Debería devolver usuario cuando existe")
        void testGetUser_Success() {
            // Given: Usuario existe
            when(userPersistencePort.findByEmail("maria@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            // When: Se obtiene el usuario
            User result = authService.getUser("maria@ejemplo.com");

            // Then: Usuario devuelto
            assertNotNull(result);
            assertEquals("maria@ejemplo.com", result.getEmail());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException cuando no existe")
        void testGetUser_NotFound() {
            // Given: Usuario no existe
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com"))
                    .thenReturn(Optional.empty());

            // When/Then: Lanza ResourceNotFoundException
            assertThrows(ResourceNotFoundException.class,
                    () -> authService.getUser("noexiste@ejemplo.com"));
        }
    }
}
