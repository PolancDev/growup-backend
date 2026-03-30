package com.growup.common.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipo de actividad en el sistema.
 * Generado desde OpenAPI spec.
 */
public enum ActivityType {
    ENROLLMENT("enrollment"),
    QUESTION("question"),
    REVIEW("review");

    private final String value;

    ActivityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ActivityType fromValue(String value) {
        for (ActivityType type : ActivityType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ActivityType: " + value);
    }
}