package com.growup.common.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Nivel de dificultad del curso.
 * Generado desde OpenAPI spec.
 */
public enum CourseLevel {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado");

    private final String value;

    CourseLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static CourseLevel fromValue(String value) {
        for (CourseLevel level : CourseLevel.values()) {
            if (level.value.equalsIgnoreCase(value) || level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid CourseLevel: " + value);
    }
}