package com.growup.enrollment.infrastructure.adapter.persistence.jpa.repository;

import com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity.ReviewJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad ReviewJpaEntity.
 */
@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, UUID> {

    List<ReviewJpaEntity> findByCourseId(UUID courseId);

    // Nota: findByCourseInstructorId y getAverageRatingByInstructor eliminados
    // porque dependen de CourseJpaEntity que no está en este contexto
    // Si se necesitan, usar el servicio de Course

    @Query("SELECT AVG(r.rating) FROM ReviewJpaEntity r WHERE r.courseId = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") UUID courseId);
}