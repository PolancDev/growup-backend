package com.growup.enrollment.infrastructure.adapter.web;

import com.growup.enrollment.application.service.EnrollmentService;
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
 * Adaptador Web para Inscripciones.
 * Endpoints REST para gestión de enrollments.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Enrollment management endpoints")
public class EnrollmentWebAdapter {

    private final EnrollmentService enrollmentService;

    @GetMapping("/enrollments")
    @Operation(summary = "Get student enrollments", description = "Get all enrollments for current student")
    public ResponseEntity<List<Map<String, Object>>> getStudentEnrollments(
            @Parameter(description = "Student UUID") @RequestParam UUID studentId) {
        log.info("GrowUp-Log: Get enrollments for student: {}", studentId);
        
        var enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments.stream()
                .map(this::mapEnrollmentToDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/enrollments")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Enroll in a course", description = "Create a new enrollment")
    public ResponseEntity<Map<String, Object>> enrollCourse(@RequestBody Map<String, Object> enrollmentData) {
        log.info("GrowUp-Log: Creating enrollment: {}", enrollmentData);
        
        UUID studentId = UUID.fromString(enrollmentData.get("studentId").toString());
        UUID courseId = UUID.fromString(enrollmentData.get("courseId").toString());
        
        var enrollment = enrollmentService.enrollStudent(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapEnrollmentToDto(enrollment));
    }

    @GetMapping("/enrollments/{id}")
    @Operation(summary = "Check enrollment", description = "Check if student is enrolled")
    public ResponseEntity<Map<String, Object>> checkEnrollment(
            @PathVariable UUID id,
            @RequestParam UUID studentId,
            @RequestParam UUID courseId) {
        log.info("GrowUp-Log: Check enrollment: student={}, course={}", studentId, courseId);
        
        boolean enrolled = enrollmentService.isStudentEnrolled(studentId, courseId);
        Map<String, Object> response = new HashMap<>();
        response.put("enrolled", enrolled);
        response.put("studentId", studentId);
        response.put("courseId", courseId);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapEnrollmentToDto(com.growup.enrollment.domain.model.Enrollment enrollment) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", enrollment.getId());
        dto.put("studentId", enrollment.getStudentId());
        dto.put("courseId", enrollment.getCourseId());
        dto.put("progress", enrollment.getProgress());
        dto.put("enrollmentStatus", enrollment.getEnrollmentStatus());
        dto.put("lastAccessDate", enrollment.getLastAccessDate());
        dto.put("nextLessonId", enrollment.getNextLessonId());
        return dto;
    }
}
