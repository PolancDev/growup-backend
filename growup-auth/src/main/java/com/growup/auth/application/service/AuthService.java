package com.growup.auth.application.service;

import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.out.TokenGeneratorPort;
import com.growup.auth.domain.port.out.UserPersistencePort;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de Aplicación para Autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserPersistencePort userPersistencePort;
    private final TokenGeneratorPort tokenGeneratorPort;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User login(String email, String password) {
        log.info("GrowUp-Log: AuthService - Intentando login para usuario: {}", email);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado tras autenticación exitosa"));
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