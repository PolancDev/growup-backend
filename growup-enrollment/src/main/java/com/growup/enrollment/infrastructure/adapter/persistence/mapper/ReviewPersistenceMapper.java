package com.growup.enrollment.infrastructure.adapter.persistence.mapper;

import com.growup.enrollment.domain.model.Review;
import com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity.ReviewJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre el modelo de dominio Review y la entidad JPA
 * ReviewJpaEntity.
 */
@Component
public class ReviewPersistenceMapper {

    public Review toDomain(ReviewJpaEntity entity) {
        if (entity == null)
            return null;

        return Review.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .studentId(entity.getStudentId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    public ReviewJpaEntity toEntity(Review domain) {
        if (domain == null)
            return null;

        return ReviewJpaEntity.builder()
                .id(domain.getId())
                .courseId(domain.getCourseId())
                .studentId(domain.getStudentId())
                .rating(domain.getRating())
                .comment(domain.getComment())
                .version(domain.getVersion() != null ? domain.getVersion() : 0L)
                .build();
    }
}