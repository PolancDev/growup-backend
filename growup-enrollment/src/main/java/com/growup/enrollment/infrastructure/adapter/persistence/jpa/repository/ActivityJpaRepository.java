package com.growup.enrollment.infrastructure.adapter.persistence.jpa.repository;

import com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity.ActivityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad ActivityJpaEntity.
 */
@Repository
public interface ActivityJpaRepository extends JpaRepository<ActivityJpaEntity, UUID> {

    List<ActivityJpaEntity> findByUserIdOrderByTimeDesc(UUID userId);
}