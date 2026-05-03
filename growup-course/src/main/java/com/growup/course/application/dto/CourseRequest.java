package com.growup.course.application.dto;

import com.growup.common.domain.model.enums.CourseLevel;
import com.growup.common.domain.model.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO para solicitudes de creación/actualización de curso.
 * Contiene los campos que el cliente puede enviar.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private UUID id; // Necesario para operaciones de actualización

    @NotBlank(message = "Title is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Level is required")
    private CourseLevel level;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private BigDecimal duration;
    private CourseStatus publicationStatus;
    private String imageUrl;
    private List<CourseModuleDto> syllabus; // Para crear módulos con el curso
}
