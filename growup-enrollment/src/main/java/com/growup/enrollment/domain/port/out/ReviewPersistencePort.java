package com.growup.enrollment.domain.port.out;

import com.growup.enrollment.domain.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para la persistencia de Valoraciones (Reviews).
 */
public interface ReviewPersistencePort {
    Review save(Review review);

    Optional<Review> findById(UUID id);

    List<Review> findByCourseId(UUID courseId);

    List<Review> findByInstructorId(UUID instructorId);

    Double getAverageRatingByInstructor(UUID instructorId);

    Double getAverageRatingByCourseId(UUID courseId);

    void deleteById(UUID id);
}