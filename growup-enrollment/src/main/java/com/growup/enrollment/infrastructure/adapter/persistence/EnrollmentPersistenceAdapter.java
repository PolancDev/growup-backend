package com.growup.enrollment.infrastructure.adapter.persistence;

import com.growup.enrollment.domain.model.Enrollment;
import com.growup.enrollment.domain.port.out.EnrollmentPersistencePort;
import com.growup.enrollment.infrastructure.adapter.persistence.jpa.repository.EnrollmentJpaRepository;
import com.growup.enrollment.infrastructure.adapter.persistence.mapper.EnrollmentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para inscripciones.
 */
@Component
@RequiredArgsConstructor
public class EnrollmentPersistenceAdapter implements EnrollmentPersistencePort {

    private final EnrollmentJpaRepository enrollmentRepository;
    private final EnrollmentPersistenceMapper enrollmentMapper;

    @Override
    public Enrollment save(Enrollment enrollment) {
        var entity = enrollmentMapper.toEntity(enrollment);
        var savedEntity = enrollmentRepository.save(entity);
        return enrollmentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Enrollment> findById(UUID id) {
        return enrollmentRepository.findById(id).map(enrollmentMapper::toDomain);
    }

    @Override
    public List<Enrollment> findByUserId(UUID userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(enrollmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Enrollment> findByUserAndCourse(UUID userId, UUID courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .map(enrollmentMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public List<Enrollment> findByStudentId(UUID studentId) {
        return enrollmentRepository.findByUserId(studentId).stream()
                .map(enrollmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(studentId, courseId);
    }

    @Override
    public List<Enrollment> findByInstructorId(UUID instructorId) {
        // TODO: Implementar usando llamada al servicio de Course para obtener courseIds por instructor
        return List.of();
    }

    @Override
    public List<Enrollment> findByInstructorIdAndCreatedAtAfter(UUID instructorId, OffsetDateTime date) {
        // TODO: Implementar usando llamada al servicio de Course
        return List.of();
    }
}