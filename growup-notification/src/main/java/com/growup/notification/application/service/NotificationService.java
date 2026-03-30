package com.growup.notification.application.service;

import com.growup.notification.domain.model.Notification;
import com.growup.notification.domain.port.in.NotificationInPort;
import com.growup.notification.domain.port.out.NotificationPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación para Notificaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationInPort {

    private final NotificationPersistencePort notificationPersistencePort;

    @Override
    public Notification sendNotification(UUID userId, String title, String message, String type, URI link) {
        log.info("GrowUp-Log: NotificationService - Enviando notificación a usuario {}", userId);

        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .link(link)
                .date(OffsetDateTime.now())
                .read(false)
                .build();

        return notificationPersistencePort.save(notification);
    }

    @Override
    public List<Notification> getNotificationsByUser(UUID userId) {
        return notificationPersistencePort.findByUserId(userId);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        notificationPersistencePort.findById(notificationId)
                .ifPresent(n -> {
                    n.setRead(true);
                    notificationPersistencePort.save(n);
                });
    }

    @Override
    public long countUnread(UUID userId) {
        return notificationPersistencePort.countUnreadByUserId(userId);
    }
}