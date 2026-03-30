package com.growup.enrollment.domain.port.out;

import com.growup.enrollment.domain.model.Activity;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de Salida para el registro de actividades.
 */
public interface ActivityPersistencePort {
    Activity save(Activity activity);

    List<Activity> findByUserIdOrderByTimeDesc(UUID userId);
}