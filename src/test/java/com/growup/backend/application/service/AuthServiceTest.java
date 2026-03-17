package com.growup.backend.application.service;

import com.growup.backend.domain.model.Role;
import com.growup.backend.domain.model.User;
import com.growup.backend.domain.port.out.TokenGeneratorPort;
import com.growup.backend.domain.port.out.UserPersistencePort;
import com.growup.backend.infrastructure.exception.ResourceNotFoundException;
import com.growup.backend.model.UpdateUserRequest;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doAnswer;

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
                .build();
    }

    @Nested
    @DisplayName("Pruebas para registro de usuario (register)")
    class RegisterTests {
        @Test
        @DisplayName("Debería registrar usuario correctamente cuando el email no existe")
        void shouldRegisterUserSuccessfully() {
            when(userPersistencePort.findByEmail("nuevo@ejemplo.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userPersistencePort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User newUser = User.builder()
                    .name("Nuevo Usuario")
                    .email("nuevo@ejemplo.com")
                    .role(Role.STUDENT)
                    .build();

            User result = authService.register(newUser, "password123");

            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals("nuevo@ejemplo.com", result.getEmail());
            verify(userPersistencePort).save(any(User.class));
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando el email ya existe")
        void shouldThrowExceptionWhenEmailExists() {
            when(userPersistencePort.findByEmail("existente@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            User newUser = User.builder()
                    .email("existente@ejemplo.com")
                    .build();

            assertThrows(IllegalArgumentException.class, 
                () -> authService.register(newUser, "password123"));
        }
    }

    @Nested
    @DisplayName("Pruebas para login (login)")
    class LoginTests {
        @Test
        @DisplayName("Debería hacer login correctamente cuando credenciales son válidas")
        void shouldLoginSuccessfully() {
            when(userPersistencePort.findByEmail("maria@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            User result = authService.login("maria@ejemplo.com", "password123");

            assertNotNull(result);
            assertEquals("maria@ejemplo.com", result.getEmail());
            verify(authenticationManager).authenticate(any());
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando el usuario no existe después de autenticación")
        void shouldThrowExceptionWhenUserNotFoundAfterAuth() {
            doAnswer(inv -> null).when(authenticationManager).authenticate(any());
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                () -> authService.login("noexiste@ejemplo.com", "password123"));
        }
    }

    @Nested
    @DisplayName("Pruebas para generación de token (generateToken)")
    class GenerateTokenTests {
        @Test
        @DisplayName("Debería generar token correctamente")
        void shouldGenerateToken() {
            when(tokenGeneratorPort.generateToken(sampleUser)).thenReturn("jwt.token.string");

            String result = authService.generateToken(sampleUser);

            assertEquals("jwt.token.string", result);
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener usuario por email (getUser)")
    class GetUserTests {
        @Test
        @DisplayName("Debería devolver usuario cuando existe")
        void shouldReturnUserWhenExists() {
            when(userPersistencePort.findByEmail("maria@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            User result = authService.getUser("maria@ejemplo.com");

            assertNotNull(result);
            assertEquals("maria@ejemplo.com", result.getEmail());
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando no existe")
        void shouldThrowExceptionWhenNotExists() {
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                () -> authService.getUser("noexiste@ejemplo.com"));
        }
    }

    @Nested
    @DisplayName("Pruebas para actualización de perfil (updateProfile)")
    class UpdateProfileTests {
        @Test
        @DisplayName("Debería actualizar nombre correctamente")
        void shouldUpdateName() {
            when(userPersistencePort.findByEmail("maria@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));
            when(userPersistencePort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateUserRequest request = new UpdateUserRequest();
            request.setName("María Actualizada");

            User result = authService.updateProfile("maria@ejemplo.com", request);

            assertEquals("María Actualizada", result.getName());
        }

        @Test
        @DisplayName("Debería actualizar contraseña correctamente")
        void shouldUpdatePassword() {
            when(userPersistencePort.findByEmail("maria@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));
            when(passwordEncoder.encode("newpassword")).thenReturn("hashedNewPassword");
            when(userPersistencePort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateUserRequest request = new UpdateUserRequest();
            request.setPassword("newpassword");

            User result = authService.updateProfile("maria@ejemplo.com", request);

            verify(passwordEncoder).encode("newpassword");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando usuario no existe")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                () -> authService.updateProfile("noexiste@ejemplo.com", new UpdateUserRequest()));
        }
    }
}
