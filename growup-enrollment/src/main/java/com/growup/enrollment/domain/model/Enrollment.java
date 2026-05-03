package com.growup.enrollment.domain.model;

import com.growup.common.domain.model.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio puro para la Inscripción.
 * No contiene anotaciones de persistencia ni de frameworks externos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private Integer progress;
    private OffsetDateTime lastAccessDate;
    private EnrollmentStatus enrollmentStatus;
    private UUID nextLessonId;
    private OffsetDateTime createdAt;
    private Long version;
}