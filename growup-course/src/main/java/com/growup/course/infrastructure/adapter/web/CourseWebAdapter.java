package com.growup.course.infrastructure.adapter.web;

import com.growup.course.application.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve all courses with optional filters")
    public ResponseEntity<List<Map<String, Object>>> getCourses(
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by level") @RequestParam(required = false) String level,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {
        log.info("GrowUp-Log: CourseWebAdapter - Buscando cursos con filtros: {}, {}, {}", category, level, status);

        var domainCourses = courseService.getAllCourses(null, category, level, status);

        return ResponseEntity.ok(domainCourses.stream()
                .map(this::mapCourseToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its UUID")
    public ResponseEntity<Map<String, Object>> getCourseById(
            @Parameter(description = "Course UUID") @PathVariable UUID id) {
        log.info("GrowUp-Log: CourseWebAdapter - Obteniendo detalles del curso con ID: {}", id);
        var domainCourse = courseService.getCourseById(id);
        return ResponseEntity.ok(mapCourseToDto(domainCourse));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new course", description = "Create a new course (requires TEACHER or ADMIN role)")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Map<String, Object> courseData) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("GrowUp-Log: CourseWebAdapter - Creando curso por: {}", currentEmail);

        var domainCourse = mapDtoToCourse(courseData);
        // Note: instructorId debería obtenerse del token de autenticación
        // Por ahora, se necesita implementar la lógica para obtener el instructor ID
        
        var created = courseService.createCourse(domainCourse, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapCourseToDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Update a course", description = "Update an existing course (requires TEACHER or ADMIN role)")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @Parameter(description = "Course UUID") @PathVariable UUID id,
            @RequestBody Map<String, Object> courseData) {
        log.info("GrowUp-Log: CourseWebAdapter - Actualizando curso: {}", id);
        var domainCourse = mapDtoToCourse(courseData);
        var updated = courseService.updateCourse(id, domainCourse);
        return ResponseEntity.ok(mapCourseToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Delete a course", description = "Delete a course (requires ADMIN or TEACHER role)")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course UUID") @PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/syllabus")
    @Operation(summary = "Get course syllabus", description = "Retrieve the syllabus (modules and topics) of a course")
    public ResponseEntity<List<Map<String, Object>>> getCourseSyllabus(
            @Parameter(description = "Course UUID") @PathVariable UUID id) {
        log.info("GrowUp-Log: CourseWebAdapter - Obteniendo syllabus del curso: {}", id);
        var domainCourse = courseService.getCourseById(id);
        var dto = mapCourseToDto(domainCourse);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> syllabus = (List<Map<String, Object>>) dto.get("syllabus");
        return ResponseEntity.ok(syllabus);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapCourseToDto(com.growup.course.domain.model.Course course) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", course.getId());
        dto.put("name", course.getName());
        dto.put("description", course.getDescription());
        dto.put("imageUrl", course.getImageUrl());
        dto.put("category", course.getCategory());
        dto.put("level", course.getLevel());
        dto.put("price", course.getPrice());
        dto.put("duration", course.getDuration());
        dto.put("startDate", course.getStartDate());
        dto.put("endDate", course.getEndDate());
        dto.put("publicationStatus", course.getPublicationStatus());
        dto.put("createdAt", course.getCreatedAt());
        dto.put("updatedAt", course.getUpdatedAt());
        dto.put("instructorId", course.getInstructorId());
        dto.put("enrolledCount", course.getEnrolledCount());
        dto.put("version", course.getVersion());
        
        if (course.getSyllabus() != null) {
            dto.put("syllabus", course.getSyllabus().stream()
                    .map(this::mapModuleToDto)
                    .collect(Collectors.toList()));
        } else {
            dto.put("syllabus", List.of());
        }
        
        return dto;
    }

    private Map<String, Object> mapModuleToDto(com.growup.course.domain.model.CourseModule module) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", module.getId());
        dto.put("title", module.getTitle());
        dto.put("description", module.getDescription());
        dto.put("order", module.getOrder());
        dto.put("version", module.getVersion());
        
        if (module.getTopics() != null) {
            dto.put("topics", module.getTopics().stream()
                    .map(this::mapTopicToDto)
                    .collect(Collectors.toList()));
        } else {
            dto.put("topics", List.of());
        }
        
        return dto;
    }

    private Map<String, Object> mapTopicToDto(com.growup.course.domain.model.Topic topic) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", topic.getId());
        dto.put("title", topic.getName());
        dto.put("duration", topic.getDuration());
        dto.put("isFree", topic.getIsFree());
        dto.put("version", topic.getVersion());
        return dto;
    }

    @SuppressWarnings("unchecked")
    private com.growup.course.domain.model.Course mapDtoToCourse(Map<String, Object> dto) {
        com.growup.course.domain.model.Course course = new com.growup.course.domain.model.Course();
        
        if (dto.get("id") != null) {
            course.setId(UUID.fromString(dto.get("id").toString()));
        }
        course.setName((String) dto.get("name"));
        course.setDescription((String) dto.get("description"));
        course.setCategory((String) dto.get("category"));
        course.setLevel((String) dto.get("level"));
        
        if (dto.get("price") != null) {
            course.setPrice(Double.parseDouble(dto.get("price").toString()));
        }
        course.setPublicationStatus((String) dto.get("publicationStatus"));
        
        return course;
    }
}