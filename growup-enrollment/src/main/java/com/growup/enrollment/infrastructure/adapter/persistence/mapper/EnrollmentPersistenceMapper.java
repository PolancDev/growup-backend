package com.growup.enrollment.infrastructure.adapter.persistence.mapper;

import com.growup.common.domain.model.enums.EnrollmentStatus;
import com.growup.enrollment.domain.model.Enrollment;
import com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity.EnrollmentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre el modelo de dominio Enrollment y la entidad JPA
 * EnrollmentJpaEntity.
 */
@Component
public class EnrollmentPersistenceMapper {

    public Enrollment toDomain(EnrollmentJpaEntity entity) {
        if (entity == null)
            return null;

        Enrollment enrollment = new Enrollment();
        enrollment.setId(entity.getId());
        enrollment.setStudentId(entity.getUserId());
        enrollment.setCourseId(entity.getCourseId());
        enrollment.setProgress(entity.getProgress());
        enrollment.setLastAccessDate(entity.getLastAccessDate());
        enrollment.setEnrollmentStatus(mapStatus(entity.getEnrollmentStatus()));
        enrollment.setNextLessonId(entity.getNextLessonId());
        enrollment.setCreatedAt(entity.getCreatedAt());
        enrollment.setVersion(entity.getVersion());
        return enrollment;
    }

    public EnrollmentJpaEntity toEntity(Enrollment enrollment) {
        if (enrollment == null)
            return null;

        EnrollmentJpaEntity entity = new EnrollmentJpaEntity();
        entity.setId(enrollment.getId());
        entity.setUserId(enrollment.getStudentId());
        entity.setCourseId(enrollment.getCourseId());
        entity.setProgress(enrollment.getProgress());
        entity.setLastAccessDate(enrollment.getLastAccessDate());
        entity.setEnrollmentStatus(mapStatus(enrollment.getEnrollmentStatus()));
        entity.setNextLessonId(enrollment.getNextLessonId());
        entity.setCreatedAt(enrollment.getCreatedAt());
        entity.setVersion(enrollment.getVersion() != null ? enrollment.getVersion() : 0L);
        return entity;
    }

    private String mapStatus(EnrollmentStatus status) {
        return status != null ? status.getValue() : null;
    }

    private EnrollmentStatus mapStatus(String status) {
        if (status == null)
            return null;
        try {
            return EnrollmentStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}