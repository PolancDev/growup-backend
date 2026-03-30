package com.growup.common.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estado de inscripción del estudiante en un curso.
 * Generado desde OpenAPI spec.
 */
public enum EnrollmentStatus {
    ACTIVE("active"),
    COMPLETED("completed"),
    ARCHIVED("archived"),
    NOT_STARTED("not_started");

    private final String value;

    EnrollmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static EnrollmentStatus fromValue(String value) {
        for (EnrollmentStatus status : EnrollmentStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EnrollmentStatus: " + value);
    }
}