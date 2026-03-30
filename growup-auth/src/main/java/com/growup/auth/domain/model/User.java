package com.growup.auth.domain.model;

import com.growup.common.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio puro para el Usuario.
 * No contiene anotaciones de persistencia ni de frameworks externos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Boolean isActive;
    private Role role;
    private String bio;
    private String avatar;
    private OffsetDateTime joinDate;
    private Long version;

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}