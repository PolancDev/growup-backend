package com.growup.auth.domain.port.out;

import com.growup.auth.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para la persistencia de Usuarios (SPI).
 */
public interface UserPersistencePort {
    Optional<User> findById(UUID id);

    List<User> findAll();

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteById(UUID id);
}