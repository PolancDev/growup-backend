package com.growup.notification.infrastructure.adapter.persistence.jpa.repository;

import com.growup.notification.infrastructure.adapter.persistence.jpa.entity.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad NotificationJpaEntity.
 */
@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    List<NotificationJpaEntity> findByUserIdOrderByDateDesc(UUID userId);

    List<NotificationJpaEntity> findByUserId(UUID userId);

    long countByUserIdAndReadFalse(UUID userId);
}