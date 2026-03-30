package com.growup.common.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estado de publicación del curso.
 * Generado desde OpenAPI spec.
 */
public enum CourseStatus {
    PUBLICADO("Publicado"),
    BORRADOR("Borrador"),
    EN_REVISION("En Revision");

    private final String value;

    CourseStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static CourseStatus fromValue(String value) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CourseStatus: " + value);
    }
}