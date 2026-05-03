package com.growup.course.infrastructure.adapter.persistence.jpa.repository;

import com.growup.course.infrastructure.adapter.persistence.jpa.entity.TopicJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad TopicJpaEntity.
 */
@Repository
public interface TopicJpaRepository extends JpaRepository<TopicJpaEntity, UUID> {

    List<TopicJpaEntity> findByModuleId(UUID moduleId);

    void deleteByModuleId(UUID moduleId);
}
