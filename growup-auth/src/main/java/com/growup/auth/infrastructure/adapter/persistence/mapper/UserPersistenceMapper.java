package com.growup.auth.infrastructure.adapter.persistence.mapper;

import com.growup.auth.domain.model.User;
import com.growup.auth.infrastructure.adapter.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre el modelo de dominio User y la entidad JPA
 * UserJpaEntity.
 */
@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null)
            return null;

        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setIsActive(entity.getIsActive());
        user.setRole(entity.getRole());
        user.setBio(entity.getBio());
        user.setAvatar(entity.getAvatar());
        user.setJoinDate(entity.getJoinDate());
        user.setVersion(entity.getVersion());
        return user;
    }

    public UserJpaEntity toEntity(User user) {
        if (user == null)
            return null;

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setIsActive(user.getIsActive());
        entity.setRole(user.getRole());
        entity.setBio(user.getBio());
        entity.setAvatar(user.getAvatar());
        entity.setJoinDate(user.getJoinDate());
        entity.setVersion(user.getVersion() != null ? user.getVersion() : 0L);
        return entity;
    }
}