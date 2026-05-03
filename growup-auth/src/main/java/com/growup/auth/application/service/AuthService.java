package com.growup.auth.application.service;

import com.growup.auth.domain.exception.InvalidCredentialsException;
import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.in.AuthInPort;
import com.growup.auth.domain.port.out.TokenGeneratorPort;
import com.growup.auth.domain.port.out.UserPersistencePort;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de Aplicación para Autenticación.
 * Sigue estrictamente la Arquitectura Hexagonal.
 * Esta clase implementa el puerto de entrada AuthInPort.
 */
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthInPort {

    private final UserPersistencePort userPersistencePort;
    private final TokenGeneratorPort tokenGeneratorPort;
    private final PasswordEncoder passwordEncoder;

    public User login(String email, String rawPassword) {
        log.info("GrowUp-Log: AuthService - Intentando login para usuario: {}", email);

        // 1. Buscar usuario por email (Puerto de salida)
        log.debug("GrowUp-Log: AuthService - Buscando usuario por email: {}", email);
        Optional<User> userOpt = userPersistencePort.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.warn("GrowUp-Log: AuthService - Usuario no encontrado: {}", email);
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        
        User user = userOpt.get();
        log.debug("GrowUp-Log: AuthService - Usuario encontrado: {}, rol: {}, activo: {}", 
                user.getEmail(), user.getRole(), user.getIsActive());

        // 2. Verificar contraseña usando PasswordEncoder únicamente
        log.debug("GrowUp-Log: AuthService - Verificando contraseña para: {}", email);
        log.debug("GrowUp-Log: AuthService - Password hash almacenado: {}", user.getPassword());
        
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("GrowUp-Log: AuthService - Contraseña incorrecta para usuario: {}", email);
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        log.info("GrowUp-Log: AuthService - Login exitoso para usuario: {}", email);
        return user;
    }

    public User register(User user, String password) {
        log.info("GrowUp-Log: AuthService - Registrando nuevo usuario: {}", user);

        if (userPersistencePort.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(password));
        user.setJoinDate(OffsetDateTime.now());
        user.setIsActive(true);
        user.setVersion(0L);
        
        // Asignar rol por defecto STUDENT si no se especifica
        if (user.getRole() == null) {
            user.setRole(com.growup.common.domain.model.enums.Role.STUDENT);
        }

        return userPersistencePort.save(user);
    }

    public String generateToken(User user) {
        return tokenGeneratorPort.generateToken(user);
    }

    public User getUser(String email) {
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Obtiene un usuario por su ID.
     * Usado para obtener información del usuario desde el token JWT de Keycloak.
     */
    public Optional<User> getUserById(UUID id) {
        return userPersistencePort.findById(id);
    }

    public User updateProfile(String userId, String name, String emailNew, String password, String avatar, String bio) {
        log.info("GrowUp-Log: AuthService - Actualizando perfil para usuario: {}", userId);

        User existingUser = userPersistencePort.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (name != null && !name.isBlank()) {
            existingUser.setName(name);
        }
        if (emailNew != null && !emailNew.isBlank()) {
            existingUser.setEmail(emailNew);
        }
        if (password != null && !password.isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }
        if (avatar != null && !avatar.isBlank()) {
            existingUser.setAvatar(avatar);
        }
        if (bio != null && !bio.isBlank()) {
            existingUser.setBio(bio);
        }

        return userPersistencePort.save(existingUser);
    }
}