package com.growup.enrollment.application.service;

import com.growup.enrollment.domain.model.Review;
import com.growup.enrollment.domain.port.in.ReviewInPort;
import com.growup.enrollment.domain.port.out.ReviewPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación para la gestión de valoraciones (Reviews).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService implements ReviewInPort {

    private final ReviewPersistencePort reviewPersistencePort;

    @Override
    @Transactional
    public Review createReview(Review review) {
        return reviewPersistencePort.save(review);
    }

    @Override
    public List<Review> getReviewsByInstructor(UUID instructorId) {
        return reviewPersistencePort.findByInstructorId(instructorId);
    }
}