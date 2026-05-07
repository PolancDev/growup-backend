package com.growup.auth.domain.port.in;

import com.growup.auth.domain.model.User;
import com.growup.common.domain.model.enums.Role;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de Entrada para los casos de uso de Gestión de Usuarios.
 */
public interface UserInPort {

    /**
     * Obtiene un usuario por su ID.
     */
    User getUserById(UUID id);

    /**
     * Obtiene todos los usuarios.
     */
    List<User> getAllUsers();

    /**
     * Obtiene usuarios filtrados por rol y/o estado activo.
     * Si ambos parámetros son null, devuelve todos los usuarios.
     *
     * @param role      Filtrar por rol (opcional)
     * @param isActive  Filtrar por estado activo (opcional)
     * @return Lista de usuarios que cumplen los filtros
     */
    List<User> getAllUsersFiltered(Role role, Boolean isActive);

    /**
     * Actualiza un usuario existente.
     */
    User updateUser(UUID id, User user);

    /**
     * Obtiene el perfil de un instructor.
     */
    User getInstructorProfile(UUID id);

    /**
     * Obtiene un usuario por su email.
     */
    User getUserByEmail(String email);

    /**
     * Activa o desactiva un usuario.
     */
    User toggleUserStatus(UUID id);
}
