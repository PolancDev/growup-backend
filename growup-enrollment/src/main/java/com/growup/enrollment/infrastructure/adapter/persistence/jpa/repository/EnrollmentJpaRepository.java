package com.growup.enrollment.infrastructure.adapter.persistence.jpa.repository;

import com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity.EnrollmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad EnrollmentJpaEntity.
 */
@Repository
public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, UUID> {

    List<EnrollmentJpaEntity> findByUserId(UUID userId);

    Optional<EnrollmentJpaEntity> findByUserIdAndCourseId(UUID userId, UUID courseId);

    default List<EnrollmentJpaEntity> findByStudentId(UUID studentId) {
        return findByUserId(studentId);
    }

    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    // Nota: findByCourseInstructorId y findByCourseInstructorIdAndCreatedAtAfter eliminados
    // porque requieren acceso al instructorId del curso, que está en otro microservicio
}