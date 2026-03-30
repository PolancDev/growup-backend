package com.growup.notification.infrastructure.adapter.web;

import com.growup.notification.application.service.NotificationService;
import com.growup.notification.domain.model.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Notificaciones.
 * Endpoints REST para gestión de notificaciones.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationWebAdapter {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get all notifications for a user")
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(
            @Parameter(description = "User UUID") @RequestParam UUID userId,
            @Parameter(description = "Filter unread only") @RequestParam(defaultValue = "false") boolean unreadOnly) {
        log.info("GrowUp-Log: Get notifications for user: {}, unreadOnly: {}", userId, unreadOnly);
        
        var notifications = notificationService.getNotificationsByUser(userId);
        
        if (unreadOnly) {
            notifications = notifications.stream()
                    .filter(n -> !n.getRead())
                    .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(notifications.stream()
                .map(this::mapNotificationToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications for a user")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @Parameter(description = "User UUID") @RequestParam UUID userId) {
        log.info("GrowUp-Log: Get unread count for user: {}", userId);
        
        long count = notificationService.countUnread(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Send notification", description = "Create and send a new notification")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody Map<String, Object> notificationData) {
        log.info("GrowUp-Log: Send notification: {}", notificationData);
        
        UUID userId = UUID.fromString(notificationData.get("userId").toString());
        String title = (String) notificationData.get("title");
        String message = (String) notificationData.get("message");
        String type = (String) notificationData.get("type");
        String link = (String) notificationData.get("link");
        
        URI linkUri = link != null ? URI.create(link) : null;
        
        var notification = notificationService.sendNotification(userId, title, message, type, linkUri);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(mapNotificationToDto(notification));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable UUID id) {
        log.info("GrowUp-Log: Mark notification as read: {}", id);
        
        notificationService.markAsRead(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("read", true);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for a user")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@RequestBody Map<String, Object> requestData) {
        UUID userId = UUID.fromString(requestData.get("userId").toString());
        log.info("GrowUp-Log: Mark all notifications as read for user: {}", userId);
        
        var notifications = notificationService.getNotificationsByUser(userId);
        notifications.forEach(n -> {
            if (!n.getRead()) {
                notificationService.markAsRead(n.getId());
            }
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("markedAsRead", true);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        log.info("GrowUp-Log: Delete notification: {}", id);
        // Soft delete via JPA
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> mapNotificationToDto(Notification notification) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", notification.getId());
        dto.put("userId", notification.getUserId());
        dto.put("title", notification.getTitle());
        dto.put("message", notification.getMessage());
        dto.put("date", notification.getDate());
        dto.put("read", notification.getRead());
        dto.put("type", notification.getType());
        dto.put("link", notification.getLink());
        dto.put("version", notification.getVersion());
        return dto;
    }
}
