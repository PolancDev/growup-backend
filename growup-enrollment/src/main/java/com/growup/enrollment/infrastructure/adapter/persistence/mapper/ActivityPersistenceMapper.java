package com.growup.enrollment.infrastructure.adapter.persistence.mapper;

import com.growup.common.domain.model.enums.ActivityType;
import com.growup.enrollment.domain.model.Activity;
import com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity.ActivityJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre el modelo de dominio Activity y la entidad JPA
 * ActivityJpaEntity.
 */
@Component
public class ActivityPersistenceMapper {

    public Activity toDomain(ActivityJpaEntity entity) {
        if (entity == null)
            return null;

        Activity activity = new Activity();
        activity.setId(entity.getId());
        activity.setUserId(entity.getUserId());
        activity.setType(mapType(entity.getType()));
        activity.setAction(entity.getAction());
        activity.setTarget(entity.getTarget());
        activity.setTime(entity.getTime());
        activity.setVersion(entity.getVersion());
        return activity;
    }

    public ActivityJpaEntity toEntity(Activity domain) {
        if (domain == null)
            return null;

        ActivityJpaEntity entity = new ActivityJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setType(mapType(domain.getType()));
        entity.setAction(domain.getAction());
        entity.setTarget(domain.getTarget());
        entity.setTime(domain.getTime());
        entity.setVersion(domain.getVersion() != null ? domain.getVersion() : 0L);
        return entity;
    }

    private ActivityType mapType(String type) {
        if (type == null)
            return null;
        try {
            return ActivityType.fromValue(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String mapType(ActivityType type) {
        return type != null ? type.getValue() : null;
    }
}