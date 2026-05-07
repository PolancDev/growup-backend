package com.growup.auth.infrastructure.adapter.persistence;

import com.growup.auth.domain.model.User;
import com.growup.auth.domain.port.out.UserPersistencePort;
import com.growup.auth.infrastructure.adapter.persistence.jpa.repository.UserJpaRepository;
import com.growup.auth.infrastructure.adapter.persistence.mapper.UserPersistenceMapper;
import com.growup.common.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para usuarios.
 * Implementa UserPersistencePort delegando en UserJpaRepository.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository userRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public User save(User user) {
        log.info("Saving user: {}", user);
        var entity = userMapper.toEntity(user);
        var savedEntity = userRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Usar búsqueda case-insensitive para evitar problemas de login
        return userRepository.findByEmailIgnoreCase(email).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByIsActive(Boolean isActive) {
        return userRepository.findByIsActive(isActive).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRoleAndIsActive(Role role, Boolean isActive) {
        return userRepository.findByRoleAndIsActive(role, isActive).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }
}