package com.growup.notification.infrastructure.adapter.persistence.mapper;

import com.growup.common.domain.model.enums.NotificationType;
import com.growup.notification.domain.model.Notification;
import com.growup.notification.infrastructure.adapter.persistence.jpa.entity.NotificationJpaEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Mapper para convertir entre el modelo de dominio Notification y la entidad JPA
 * NotificationJpaEntity.
 */
@Component
public class NotificationPersistenceMapper {

    public Notification toDomain(NotificationJpaEntity entity) {
        if (entity == null)
            return null;

        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(mapType(entity.getType()))
                .read(entity.getRead())
                .date(entity.getDate())
                .link(toUri(entity.getLink()))
                .version(entity.getVersion())
                .build();
    }

    public NotificationJpaEntity toEntity(Notification domain) {
        if (domain == null)
            return null;

        return NotificationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .message(domain.getMessage())
                .type(mapType(domain.getType()))
                .read(domain.getRead())
                .date(domain.getDate())
                .link(toString(domain.getLink()))
                .version(domain.getVersion() != null ? domain.getVersion() : 0L)
                .build();
    }

    private URI toUri(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return URI.create(value);
        } catch (Exception e) {
            return null;
        }
    }

    private String toString(URI value) {
        return value != null ? value.toString() : null;
    }

    private NotificationType mapType(String type) {
        if (type == null)
            return null;
        try {
            return NotificationType.fromValue(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String mapType(NotificationType type) {
        return type != null ? type.getValue() : null;
    }
}