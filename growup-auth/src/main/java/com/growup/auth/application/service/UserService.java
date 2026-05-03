package com.growup.auth.application.service;

import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.in.UserInPort;
import com.growup.auth.domain.port.out.UserPersistencePort;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación para Usuarios.
 * Implementa el Puerto de Entrada y se comunica con el Puerto de Salida.
 * Sigue estrictamente la Arquitectura Hexagonal.
 */
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserInPort {

    private final UserPersistencePort userPersistencePort;

    public User getUserById(UUID id) {
        log.info("GrowUp-Log: UserService - Buscando usuario por ID: {}", id);
        return userPersistencePort.findById(id)
                .orElseThrow(() -> {
                    log.error("GrowUp-Log: UserService - Usuario no encontrado: {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
                });
    }

    public List<User> getAllUsers() {
        log.info("GrowUp-Log: UserService - Listando todos los usuarios");
        return userPersistencePort.findAll();
    }

    public User updateUser(UUID id, User user) {
        log.info("GrowUp-Log: UserService - Actualizando usuario: {}", id);
        User existingUser = getUserById(id);

        existingUser.setName(user.getName());
        existingUser.setBio(user.getBio());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setVersion(user.getVersion());

        return userPersistencePort.save(existingUser);
    }

    public User getInstructorProfile(UUID id) {
        log.info("GrowUp-Log: UserService - Cargando perfil de instructor: {}", id);
        return getUserById(id);
    }

    public User getUserByEmail(String email) {
        log.info("GrowUp-Log: UserService - Buscando usuario por email: {}", email);
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("GrowUp-Log: UserService - Usuario no encontrado: {}", email);
                    return new ResourceNotFoundException("Usuario no encontrado con email: " + email);
                });
    }

    public User toggleUserStatus(UUID id) {
        log.info("GrowUp-Log: UserService - Cambiando estado del usuario: {}", id);
        User user = getUserById(id);
        
        if (Boolean.TRUE.equals(user.getIsActive())) {
            user.deactivate();
        } else {
            user.activate();
        }
        
        return userPersistencePort.save(user);
    }
}