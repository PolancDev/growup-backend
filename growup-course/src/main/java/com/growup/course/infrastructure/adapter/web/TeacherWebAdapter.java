package com.growup.course.infrastructure.adapter.web;

import com.growup.course.application.dto.CourseResponse;
import com.growup.course.application.mapper.CourseDtoMapper;
import com.growup.course.application.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para operaciones de Profesor.
 * Endpoints REST para el panel del profesor/autor de cursos.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
@Tag(name = "Profesor", description = "Endpoints para gestión del panel del profesor")
public class TeacherWebAdapter {

    private final CourseService courseService;
    private final CourseDtoMapper courseDtoMapper;

    @GetMapping("/courses")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(
        summary = "Obtener mis cursos",
        description = "Obtener lista de cursos creados por el profesor o admin autenticado"
    )
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        UUID currentUserId = getCurrentUserId();
        log.info("GrowUp-Log: TeacherWebAdapter - Listando cursos del usuario autenticado: {}", currentUserId);

        var domainCourses = courseService.getCoursesByInstructor(currentUserId);
        return ResponseEntity.ok(domainCourses.stream()
                .map(courseDtoMapper::toResponse)
                .collect(Collectors.toList()));
    }

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return UUID.fromString(jwtAuth.getToken().getClaimAsString("sub"));
        }
        return null;
    }
}
