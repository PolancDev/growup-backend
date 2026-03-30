package com.growup.enrollment.infrastructure.adapter.persistence;

import com.growup.enrollment.domain.model.Review;
import com.growup.enrollment.domain.port.out.ReviewPersistencePort;
import com.growup.enrollment.infrastructure.adapter.persistence.jpa.repository.ReviewJpaRepository;
import com.growup.enrollment.infrastructure.adapter.persistence.mapper.ReviewPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Valoraciones (Reviews).
 */
@Component
@RequiredArgsConstructor
public class ReviewPersistenceAdapter implements ReviewPersistencePort {

    private final ReviewJpaRepository reviewRepository;
    private final ReviewPersistenceMapper reviewMapper;

    @Override
    public Review save(Review review) {
        var entity = reviewMapper.toEntity(review);
        var savedEntity = reviewRepository.save(entity);
        return reviewMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return reviewRepository.findById(id).map(reviewMapper::toDomain);
    }

    @Override
    public List<Review> findByCourseId(UUID courseId) {
        return reviewRepository.findByCourseId(courseId).stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByInstructorId(UUID instructorId) {
        // TODO: Implementar usando llamada al servicio de Course
        return List.of();
    }

    @Override
    public Double getAverageRatingByInstructor(UUID instructorId) {
        // TODO: Implementar usando llamada al servicio de Course
        return null;
    }

    @Override
    public Double getAverageRatingByCourseId(UUID courseId) {
        return reviewRepository.getAverageRatingByCourseId(courseId);
    }

    @Override
    public void deleteById(UUID id) {
        reviewRepository.deleteById(id);
    }
}