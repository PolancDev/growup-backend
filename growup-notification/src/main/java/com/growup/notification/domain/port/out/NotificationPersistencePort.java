package com.growup.notification.domain.port.out;

import com.growup.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para la persistencia de Notificaciones.
 */
public interface NotificationPersistencePort {
    Notification save(Notification notification);

    List<Notification> findByUserId(UUID userId);

    Optional<Notification> findById(UUID id);

    long countUnreadByUserId(UUID userId);

    List<Notification> findByUserIdOrderByDateDesc(UUID userId);

    long countByUserIdAndReadFalse(UUID userId);

    void deleteById(UUID id);
}