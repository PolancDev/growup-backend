package com.growup.auth.infrastructure.adapter.persistence.jpa.repository;

import com.growup.auth.infrastructure.adapter.persistence.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad UserJpaEntity.
 * Búsqueda de email case-insensitive para evitar problemas de login.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserJpaEntity> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Búsqueda por email (case-sensitive por defecto de PostgreSQL).
     * Se recomienda usar findByEmailIgnoreCase para login.
     */
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}