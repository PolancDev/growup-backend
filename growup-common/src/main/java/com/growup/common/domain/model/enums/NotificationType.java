package com.growup.common.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipo de notificación.
 * Generado desde OpenAPI spec.
 */
public enum NotificationType {
    INFO("info"),
    SUCCESS("success"),
    WARNING("warning"),
    ERROR("error");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static NotificationType fromValue(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid NotificationType: " + value);
    }
}