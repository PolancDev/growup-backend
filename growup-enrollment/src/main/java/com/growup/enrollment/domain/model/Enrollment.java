package com.growup.enrollment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio puro para la Inscripción.
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
    private String enrollmentStatus;
    private UUID nextLessonId;
    private OffsetDateTime createdAt;
    private Long version;
}