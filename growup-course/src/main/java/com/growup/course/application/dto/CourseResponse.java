package com.growup.course.application.dto;

import com.growup.common.domain.model.enums.CourseLevel;
import com.growup.common.domain.model.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuestas con información completa del curso.
 * Incluye todos los campos que el cliente necesita recibir.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private CourseLevel level;
    private Double price;
    private BigDecimal duration;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private CourseStatus publicationStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID instructorId;
    private Integer enrolledCount;
    private Long version;
    private List<CourseModuleDto> syllabus;
}
