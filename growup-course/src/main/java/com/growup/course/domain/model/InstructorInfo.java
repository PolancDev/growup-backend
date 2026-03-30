package com.growup.course.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.UUID;

/**
 * Value Object que representa la información pública del instructor.
 * Se usa para desacoplar Course de User en el dominio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorInfo {
    private UUID id;
    private String name;
    private String bio;
    private URI avatarUrl;
}