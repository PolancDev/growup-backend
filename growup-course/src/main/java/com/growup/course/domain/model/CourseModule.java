package com.growup.course.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Modelo de dominio para un Módulo de Curso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseModule {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer order;
    private List<Topic> topics;
    private Long version;
}