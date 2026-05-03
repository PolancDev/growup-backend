package com.growup.common.domain.model.enums;

/**
 * Roles de usuario en el sistema.
 * Compartido entre auth y otros módulos.
 * Dominio puro: sin anotaciones de frameworks externos (como Jackson).
 */
public enum Role {
    ADMIN("ADMIN"),
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid Role: " + value);
    }
}