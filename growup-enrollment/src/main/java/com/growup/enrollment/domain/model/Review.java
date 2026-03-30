package com.growup.enrollment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio para una Valoración (Review) de un curso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private UUID id;
    private UUID courseId;
    private UUID studentId;
    private String studentName;
    private Integer rating;
    private String comment;
    private OffsetDateTime createdAt;
    private Long version;
}