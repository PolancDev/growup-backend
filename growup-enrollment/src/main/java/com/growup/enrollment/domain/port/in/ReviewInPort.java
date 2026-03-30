package com.growup.enrollment.domain.port.in;

import com.growup.enrollment.domain.model.Review;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para el manejo de valoraciones (Reviews).
 */
public interface ReviewInPort {
    Review createReview(Review review);

    List<Review> getReviewsByInstructor(UUID instructorId);
}