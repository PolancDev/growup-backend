package com.growup.course.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Modelo de dominio para un Tema (Topic).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    private UUID id;
    private String name;
    private String duration;
    private Boolean isFree;
    private Long version;
}