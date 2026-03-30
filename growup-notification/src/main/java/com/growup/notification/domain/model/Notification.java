package com.growup.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio puro para la Notificación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private OffsetDateTime date;
    private Boolean read;
    private String type;
    private URI link;
    private Long version;
}