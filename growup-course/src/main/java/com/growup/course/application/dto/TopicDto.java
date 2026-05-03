package com.growup.course.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para temas dentro de un módulo en las respuestas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private UUID id;
    private String title;
    private String duration; // Coincide con el modelo de dominio Topic
    private Boolean isFree;
    private Long version;
}
