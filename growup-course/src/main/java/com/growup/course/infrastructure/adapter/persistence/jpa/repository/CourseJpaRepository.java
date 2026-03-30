package com.growup.course.infrastructure.adapter.persistence.jpa.repository;

import com.growup.course.infrastructure.adapter.persistence.jpa.entity.CourseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad CourseJpaEntity.
 */
@Repository
public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, UUID>, JpaSpecificationExecutor<CourseJpaEntity> {

    List<CourseJpaEntity> findByCategory(String category);

    List<CourseJpaEntity> findByInstructorId(UUID instructorId);
    
    // Nota: findByIdWithSyllabusOrdered eliminado - la relación syllabus no existe en la entidad
}