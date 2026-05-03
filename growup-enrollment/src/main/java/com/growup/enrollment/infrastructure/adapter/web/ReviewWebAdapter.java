package com.growup.enrollment.infrastructure.adapter.web;

import com.growup.enrollment.domain.model.Review;
import com.growup.enrollment.domain.port.in.ReviewInPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Valoraciones.
 * Endpoints REST para gestión de reviews.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Course review endpoints")
public class ReviewWebAdapter {

    private final ReviewInPort reviewService;

    @GetMapping("/{courseId}/reviews")
    @Operation(summary = "Get course reviews", description = "Get all reviews for a course")
    public ResponseEntity<List<Map<String, Object>>> getCourseReviews(
            @Parameter(description = "Course UUID") @PathVariable UUID courseId) {
        log.info("GrowUp-Log: Get reviews for course: {}", courseId);
        
        // Placeholder: filtrar por courseId
        var reviews = reviewService.getReviewsByInstructor(null);
        return ResponseEntity.ok(reviews.stream()
                .map(this::mapReviewToDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{courseId}/reviews")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Create review", description = "Add a review to a course")
    public ResponseEntity<Map<String, Object>> createReview(
            @PathVariable UUID courseId,
            @RequestBody Map<String, Object> reviewData) {
        log.info("GrowUp-Log: Create review for course: {}", courseId);
        
        UUID studentId = UUID.fromString(reviewData.get("studentId").toString());
        Integer rating = Integer.parseInt(reviewData.get("rating").toString());
        String comment = (String) reviewData.get("comment");
        
        Review review = Review.builder()
                .courseId(courseId)
                .studentId(studentId)
                .rating(rating)
                .comment(comment)
                .build();
        
        var created = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapReviewToDto(created));
    }

    private Map<String, Object> mapReviewToDto(Review review) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", review.getId());
        dto.put("courseId", review.getCourseId());
        dto.put("studentId", review.getStudentId());
        dto.put("rating", review.getRating());
        dto.put("comment", review.getComment());
        dto.put("createdAt", review.getCreatedAt());
        return dto;
    }
}
