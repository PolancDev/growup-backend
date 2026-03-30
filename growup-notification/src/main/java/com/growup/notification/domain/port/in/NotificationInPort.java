package com.growup.notification.domain.port.in;

import com.growup.notification.domain.model.Notification;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Puerto de Entrada para los casos de uso de Notificaciones.
 */
public interface NotificationInPort {
    Notification sendNotification(UUID userId, String title, String message, String type, URI link);

    List<Notification> getNotificationsByUser(UUID userId);

    void markAsRead(UUID notificationId);

    long countUnread(UUID userId);
}