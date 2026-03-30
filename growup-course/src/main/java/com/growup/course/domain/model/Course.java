package com.growup.course.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Modelo de dominio puro para el Curso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private UUID id;
    private String name;
    private String description;
    private URI imageUrl;
    private String category;
    private String level;
    private Double price;
    private BigDecimal duration;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String publicationStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID instructorId;
    private List<CourseModule> syllabus;
    private Integer enrolledCount;
    private Long version;
}