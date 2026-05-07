package com.growup.auth.application.service;

import com.growup.auth.domain.model.User;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserService.
 * Patrón Given-When-Then.
 */
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
                .password("encodedPassword")
                .role(Role.TEACHER)
                .isActive(true)
                .bio("Instructor de programación")
                .avatar("https://example.com/avatar.jpg")
                .joinDate(OffsetDateTime.now())
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Pruebas para obtener usuario por ID (getUserById)")
    class GetUserByIdTests {

        @Test
        @DisplayName("Debería devolver usuario cuando existe")
        void testGetUserById_Success() {
            // Given: Usuario existe
            when(userPersistencePort.findById(userId))
                    .thenReturn(Optional.of(sampleUser));

            // When: Se obtiene el usuario por ID
            User result = userService.getUserById(userId);

            // Then: Usuario devuelto
            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals("juan@ejemplo.com", result.getEmail());
            verify(userPersistencePort).findById(userId);
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException cuando ID no existe")
        void testGetUserById_NotFound() {
            // Given: Usuario no existe
            UUID nonExistentId = UUID.randomUUID();
            when(userPersistencePort.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            // When/Then: Lanza ResourceNotFoundException
            assertThrows(ResourceNotFoundException.class,
                    () -> userService.getUserById(nonExistentId));
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener todos los usuarios (getAllUsers)")
    class GetAllUsersTests {

        @Test
        @DisplayName("Debería devolver lista de usuarios")
        void testGetAllUsers_Success() {
            // Given: Hay usuarios en la base de datos
            User user2 = User.builder()
                    .id(UUID.randomUUID())
                    .name("Ana López")
                    .email("ana@ejemplo.com")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .build();

            when(userPersistencePort.findAll())
                    .thenReturn(List.of(sampleUser, user2));

            // When: Se obtienen todos los usuarios
            List<User> result = userService.getAllUsers();

            // Then: Lista devuelta correctamente
            assertEquals(2, result.size());
            verify(userPersistencePort).findAll();
        }

        @Test
        @DisplayName("Debería devolver lista vacía cuando no hay usuarios")
        void testGetAllUsers_Empty() {
            // Given: No hay usuarios
            when(userPersistencePort.findAll())
                    .thenReturn(List.of());

            // When: Se obtienen todos los usuarios
            List<User> result = userService.getAllUsers();

            // Then: Lista vacía
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener usuarios filtrados (getAllUsersFiltered)")
    class GetAllUsersFilteredTests {

        @Test
        @DisplayName("Debería filtrar por rol cuando solo se proporciona role")
        void testGetAllUsersFiltered_ByRole() {
            // Given: Se filtra por rol TEACHER
            when(userPersistencePort.findByRole(Role.TEACHER))
                    .thenReturn(List.of(sampleUser));

            // When: Se obtienen usuarios filtrados por rol
            List<User> result = userService.getAllUsersFiltered(Role.TEACHER, null);

            // Then: Solo devuelve TEACHERs
            assertEquals(1, result.size());
            assertEquals(Role.TEACHER, result.get(0).getRole());
            verify(userPersistencePort).findByRole(Role.TEACHER);
            verify(userPersistencePort, never()).findAll();
            verify(userPersistencePort, never()).findByIsActive(anyBoolean());
            verify(userPersistencePort, never()).findByRoleAndIsActive(any(), anyBoolean());
        }

        @Test
        @DisplayName("Debería filtrar por estado activo cuando solo se proporciona isActive")
        void testGetAllUsersFiltered_ByIsActive() {
            // Given: Se filtra por isActive=true
            when(userPersistencePort.findByIsActive(true))
                    .thenReturn(List.of(sampleUser));

            // When: Se obtienen usuarios activos
            List<User> result = userService.getAllUsersFiltered(null, true);

            // Then: Solo devuelve usuarios activos
            assertEquals(1, result.size());
            assertTrue(result.get(0).getIsActive());
            verify(userPersistencePort).findByIsActive(true);
            verify(userPersistencePort, never()).findAll();
            verify(userPersistencePort, never()).findByRole(any());
            verify(userPersistencePort, never()).findByRoleAndIsActive(any(), anyBoolean());
        }

        @Test
        @DisplayName("Debería filtrar por rol y estado cuando se proporcionan ambos")
        void testGetAllUsersFiltered_ByRoleAndIsActive() {
            // Given: Se filtra por rol TEACHER y isActive=true
            when(userPersistencePort.findByRoleAndIsActive(Role.TEACHER, true))
                    .thenReturn(List.of(sampleUser));

            // When: Se obtienen usuarios filtrados por ambos
            List<User> result = userService.getAllUsersFiltered(Role.TEACHER, true);

            // Then: Solo devuelve TEACHERs activos
            assertEquals(1, result.size());
            assertEquals(Role.TEACHER, result.get(0).getRole());
            assertTrue(result.get(0).getIsActive());
            verify(userPersistencePort).findByRoleAndIsActive(Role.TEACHER, true);
            verify(userPersistencePort, never()).findAll();
            verify(userPersistencePort, never()).findByRole(any());
            verify(userPersistencePort, never()).findByIsActive(anyBoolean());
        }

        @Test
        @DisplayName("Debería devolver todos los usuarios cuando no se proporcionan filtros")
        void testGetAllUsersFiltered_NoFilters() {
            // Given: No hay filtros
            when(userPersistencePort.findAll())
                    .thenReturn(List.of(sampleUser));

            // When: Se obtienen todos los usuarios sin filtros
            List<User> result = userService.getAllUsersFiltered(null, null);

            // Then: Devuelve todos los usuarios
            assertEquals(1, result.size());
            verify(userPersistencePort).findAll();
            verify(userPersistencePort, never()).findByRole(any());
            verify(userPersistencePort, never()).findByIsActive(anyBoolean());
            verify(userPersistencePort, never()).findByRoleAndIsActive(any(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("Pruebas para actualizar usuario (updateUser)")
    class UpdateUserTests {

        @Test
        @DisplayName("Debería actualizar usuario correctamente")
        void testUpdateUser_Success() {
            // Given: Usuario existe
            when(userPersistencePort.findById(userId))
                    .thenReturn(Optional.of(sampleUser));
            when(userPersistencePort.save(any(User.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When: Se actualiza el usuario
            User updateData = User.builder()
                    .name("Juan Actualizado")
                    .bio("Nueva biografía")
                    .avatar("https://new-avatar.com/avatar.jpg")
                    .version(0L)
                    .build();

            User result = userService.updateUser(userId, updateData);

            // Then: Usuario actualizado
            assertNotNull(result);
            assertEquals("Juan Actualizado", result.getName());
            assertEquals("Nueva biografía", result.getBio());
            verify(userPersistencePort).save(any(User.class));
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException cuando ID no existe")
        void testUpdateUser_NotFound() {
            // Given: Usuario no existe
            UUID nonExistentId = UUID.randomUUID();
            when(userPersistencePort.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            // When/Then: Lanza ResourceNotFoundException
            User updateData = User.builder()
                    .name("Nuevo Nombre")
                    .build();

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.updateUser(nonExistentId, updateData));
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener perfil de instructor (getInstructorProfile)")
    class GetInstructorProfileTests {

        @Test
        @DisplayName("Debería devolver perfil de instructor")
        void testGetInstructorProfile_Success() {
            // Given: Instructor existe
            when(userPersistencePort.findById(userId))
                    .thenReturn(Optional.of(sampleUser));

            // When: Se obtiene el perfil
            User result = userService.getInstructorProfile(userId);

            // Then: Perfil devuelto
            assertNotNull(result);
            assertEquals(userId, result.getId());
        }
    }

    @Nested
    @DisplayName("Pruebas para obtener usuario por email (getUserByEmail)")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Debería devolver usuario cuando existe")
        void testGetUserByEmail_Success() {
            // Given: Usuario existe
            when(userPersistencePort.findByEmail("juan@ejemplo.com"))
                    .thenReturn(Optional.of(sampleUser));

            // When: Se obtiene el usuario por email
            User result = userService.getUserByEmail("juan@ejemplo.com");

            // Then: Usuario devuelto
            assertNotNull(result);
            assertEquals("juan@ejemplo.com", result.getEmail());
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException cuando email no existe")
        void testGetUserByEmail_NotFound() {
            // Given: Usuario no existe
            when(userPersistencePort.findByEmail("noexiste@ejemplo.com"))
                    .thenReturn(Optional.empty());

            // When/Then: Lanza ResourceNotFoundException
            assertThrows(ResourceNotFoundException.class,
                    () -> userService.getUserByEmail("noexiste@ejemplo.com"));
        }
    }

    @Nested
    @DisplayName("Pruebas para cambiar estado de usuario (toggleUserStatus)")
    class ToggleUserStatusTests {

        @Test
        @DisplayName("Debería desactivar usuario activo")
        void testToggleUserStatus_Deactivate() {
            // Given: Usuario activo
            when(userPersistencePort.findById(userId))
                    .thenReturn(Optional.of(sampleUser));
            when(userPersistencePort.save(any(User.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When: Se cambia el estado
            User result = userService.toggleUserStatus(userId);

            // Then: Usuario desactivado
            assertFalse(result.getIsActive());
            verify(userPersistencePort).save(any(User.class));
        }

        @Test
        @DisplayName("Debería activar usuario inactivo")
        void testToggleUserStatus_Activate() {
            // Given: Usuario inactivo
            sampleUser.setIsActive(false);
            when(userPersistencePort.findById(userId))
                    .thenReturn(Optional.of(sampleUser));
            when(userPersistencePort.save(any(User.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When: Se cambia el estado
            User result = userService.toggleUserStatus(userId);

            // Then: Usuario activado
            assertTrue(result.getIsActive());
            verify(userPersistencePort).save(any(User.class));
        }
    }
}
