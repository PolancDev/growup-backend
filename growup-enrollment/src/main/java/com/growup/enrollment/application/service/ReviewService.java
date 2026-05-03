package com.growup.enrollment.application.service;

import com.growup.enrollment.domain.model.Review;
import com.growup.enrollment.domain.port.in.ReviewInPort;
import com.growup.enrollment.domain.port.out.ReviewPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación para la gestión de valoraciones (Reviews).
 * NO usa anotaciones de Spring (@Service) - se configura manualmente en AppConfig.
 */
@RequiredArgsConstructor
@Slf4j
public class ReviewService implements ReviewInPort {

    private final ReviewPersistencePort reviewPersistencePort;

    @Override
    public Review createReview(Review review) {
        return reviewPersistencePort.save(review);
    }

    @Override
    public List<Review> getReviewsByInstructor(UUID instructorId) {
        return reviewPersistencePort.findByInstructorId(instructorId);
    }
}