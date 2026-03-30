package com.growup.enrollment.domain.port.out;

import com.growup.enrollment.domain.model.Enrollment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para la persistencia de Inscripciones.
 */
public interface EnrollmentPersistencePort {
    Optional<Enrollment> findById(UUID id);

    List<Enrollment> findByStudentId(UUID studentId);

    Enrollment save(Enrollment enrollment);

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    void deleteById(UUID id);

    Optional<Enrollment> findByUserAndCourse(UUID userId, UUID courseId);

    List<Enrollment> findByUserId(UUID userId);

    List<Enrollment> findByInstructorId(UUID instructorId);

    List<Enrollment> findByInstructorIdAndCreatedAtAfter(UUID instructorId, OffsetDateTime date);
}