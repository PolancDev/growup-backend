package com.growup.course.infrastructure.adapter.web;

import com.growup.course.application.dto.CourseRequest;
import com.growup.course.application.dto.CourseResponse;
import com.growup.course.application.dto.UpdateCoursePriceRequest;
import com.growup.course.application.mapper.CourseDtoMapper;
import com.growup.course.application.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Cursos.
 * Endpoints REST para gestión de cursos.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseWebAdapter {

    private final CourseService courseService;
    private final CourseDtoMapper courseDtoMapper;

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve all courses with optional filters")
    public ResponseEntity<List<CourseResponse>> getCourses(
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by level") @RequestParam(required = false) String level,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {
        log.info("GrowUp-Log: CourseWebAdapter - Buscando cursos con filtros: {}, {}, {}", category, level, status);

        var domainCourses = courseService.getAllCourses(null, category, level, status);

        return ResponseEntity.ok(domainCourses.stream()
                .map(courseDtoMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its UUID")
    public ResponseEntity<CourseResponse> getCourseById(
            @Parameter(description = "Course UUID") @PathVariable UUID id) {
        log.info("GrowUp-Log: CourseWebAdapter - Obteniendo detalles del curso con ID: {}", id);
        var domainCourse = courseService.getCourseById(id);
        return ResponseEntity.ok(courseDtoMapper.toResponse(domainCourse));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new course", description = "Create a new course (requires TEACHER or ADMIN role)")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.info("GrowUp-Log: CourseWebAdapter - Creando curso por usuario: {}", currentUserId);

        var domainCourse = courseDtoMapper.toDomain(request);
        var created = courseService.createCourse(domainCourse, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseDtoMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Update a course", description = "Update an existing course (requires TEACHER or ADMIN role)")
    public ResponseEntity<CourseResponse> updateCourse(
            @Parameter(description = "Course UUID") @PathVariable UUID id,
            @Valid @RequestBody CourseRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.info("GrowUp-Log: CourseWebAdapter - Actualizando curso: {} por usuario: {}", id, currentUserId);
        var domainCourse = courseDtoMapper.toDomain(request);
        var updated = courseService.updateCourse(id, domainCourse, currentUserId);
        return ResponseEntity.ok(courseDtoMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Delete a course", description = "Delete a course (requires ADMIN or TEACHER role)")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course UUID") @PathVariable UUID id) {
        UUID currentUserId = getCurrentUserId();
        log.info("GrowUp-Log: CourseWebAdapter - Eliminando curso: {} por usuario: {}", id, currentUserId);
        courseService.deleteCourse(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/price")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Update course price", description = "Update only the price of an existing course (requires TEACHER or ADMIN role)")
    public ResponseEntity<CourseResponse> updateCoursePrice(
            @Parameter(description = "Course UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdateCoursePriceRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.info("GrowUp-Log: CourseWebAdapter - Actualizando precio del curso: {} a {} por usuario: {}",
                id, request.getPrice(), currentUserId);
        var updated = courseService.updateCoursePrice(id, request.getPrice(), currentUserId);
        return ResponseEntity.ok(courseDtoMapper.toResponse(updated));
    }

    @GetMapping("/{id}/syllabus")
    @Operation(summary = "Get course syllabus", description = "Retrieve the syllabus (modules and topics) of a course")
    public ResponseEntity<List<com.growup.course.application.dto.CourseModuleDto>> getCourseSyllabus(
            @Parameter(description = "Course UUID") @PathVariable UUID id) {
        log.info("GrowUp-Log: CourseWebAdapter - Obteniendo syllabus del curso: {}", id);
        var domainCourse = courseService.getCourseById(id);
        var response = courseDtoMapper.toResponse(domainCourse);
        return ResponseEntity.ok(response.getSyllabus());
    }

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return UUID.fromString(jwtAuth.getToken().getClaimAsString("sub"));
        }
        return null;
    }
}
