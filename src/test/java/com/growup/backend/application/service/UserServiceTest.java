package com.growup.backend.application.service;

import com.growup.backend.domain.model.Role;
import com.growup.backend.domain.model.User;
import com.growup.backend.domain.port.out.UserPersistencePort;
import com.growup.backend.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .name("Juan Pérez")
                .email("juan@ejemplo.com")
                .password("password123")
                .role(Role.STUDENT)
                .isActive(true)
                .bio("Estudiante de programación")
                .build();
    }

    @Nested
    @DisplayName("Pruebas para obtener usuario por ID (getUserById)")
    class GetUserByIdTests {
        @Test
        @DisplayName("Debería devolver el usuario cuando el ID existe")
        void shouldReturnUserWhenIdExists() {
            when(userPersistencePort.findById(userId)).thenReturn(Optional.of(sampleUser));

            User result = userService.getUserById(userId);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals("Juan Pérez", result.getName());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException cuando el ID no existe")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener todos los usuarios (getAllUsers)")
    class GetAllUsersTests {
        @Test
        @DisplayName("Debería devolver la lista de usuarios cuando existen")
        void shouldReturnUsersWhenFound() {
            User anotherUser = User.builder().id(UUID.randomUUID()).name("Ana").build();
            when(userPersistencePort.findAll()).thenReturn(Arrays.asList(sampleUser, anotherUser));

            var result = userService.getAllUsers();

            assertFalse(result.isEmpty());
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Debería devolver lista vacía cuando no hay usuarios")
        void shouldReturnEmptyListWhenNoUsers() {
            when(userPersistencePort.findAll()).thenReturn(Collections.emptyList());

            var result = userService.getAllUsers();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pruebas para actualizar usuario (updateUser)")
    class UpdateUserTests {
        @Test
        @DisplayName("Debería actualizar los campos correctamente")
        void shouldUpdateUserFields() {
            when(userPersistencePort.findById(userId)).thenReturn(Optional.of(sampleUser));
            when(userPersistencePort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User updatedData = User.builder()
                    .name("Juan Actualizado")
                    .bio("Nueva biografía")
                    .version(1L)
                    .build();

            User result = userService.updateUser(userId, updatedData);

            assertEquals("Juan Actualizado", result.getName());
            assertEquals("Nueva biografía", result.getBio());
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando el usuario no existe")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                () -> userService.updateUser(userId, new User()));
        }
    }

    @Nested
    @DisplayName("Pruebas para buscar usuario por email (getUserByEmail)")
    class GetUserByEmailTests {
        @Test
        @DisplayName("Debería devolver el usuario cuando el email existe")
        void shouldReturnUserWhenEmailExists() {
            when(userPersistencePort.findByEmail("juan@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            User result = userService.getUserByEmail("juan@ejemplo.com");

            assertNotNull(result);
            assertEquals("juan@ejemplo.com", result.getEmail());
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando el email no existe")
        void shouldThrowExceptionWhenEmailNotFound() {
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                () -> userService.getUserByEmail("noexiste@ejemplo.com"));
        }
    }

    @Nested
    @DisplayName("Pruebas para perfil de instructor (getInstructorProfile)")
    class GetInstructorProfileTests {
        @Test
        @DisplayName("Debería devolver el usuario como instructor")
        void shouldReturnInstructorProfile() {
            when(userPersistencePort.findById(userId)).thenReturn(Optional.of(sampleUser));

            User result = userService.getInstructorProfile(userId);

            assertNotNull(result);
            assertEquals(userId, result.getId());
        }
    }
}
