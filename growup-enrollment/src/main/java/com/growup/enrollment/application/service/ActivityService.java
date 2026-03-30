package com.growup.enrollment.application.service;

import com.growup.enrollment.domain.model.Activity;
import com.growup.enrollment.domain.port.in.ActivityInPort;
import com.growup.enrollment.domain.port.out.ActivityPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación para el registro de actividades.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService implements ActivityInPort {

    private final ActivityPersistencePort activityPersistencePort;

    @Override
    public void logActivity(UUID userId, String type, String action, String target) {
        log.info("GrowUp-Log: ActivityService - Registrando actividad para usuario {}", userId);

        Activity activity = Activity.builder()
                .userId(userId)
                .type(type)
                .action(action)
                .target(target)
                .time(OffsetDateTime.now())
                .build();

        activityPersistencePort.save(activity);
    }

    @Override
    public List<Activity> getActivitiesByUser(UUID userId) {
        log.info("GrowUp-Log: ActivityService - Buscando actividades para usuario {}", userId);
        return activityPersistencePort.findByUserIdOrderByTimeDesc(userId);
    }
}