package com.growup.course.application.service;

import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import com.growup.course.domain.model.Course;
import com.growup.course.domain.port.in.CourseInPort;
import com.growup.course.domain.port.out.CoursePersistencePort;
import com.growup.course.domain.port.out.InstructorLookupPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación para Cursos.
 * Coordina la creación, actualización y consulta de cursos usando Puertos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService implements CourseInPort {

    private final CoursePersistencePort coursePersistencePort;
    private final InstructorLookupPort instructorLookupPort;

    @Override
    @Transactional(readOnly = true)
    public List<Course> getAllCourses(UUID instructorId, String category, String level, String status) {
        String cat = (category == null || category.trim().isEmpty()) ? null : category;
        String lev = (level == null || level.trim().isEmpty()) ? null : level;
        String sta = (status == null || status.trim().isEmpty()) ? null : status;

        log.info("GrowUp-Log: CourseService - Listando cursos con filtros: {}, {}, {}",
                cat, lev, sta);
        return coursePersistencePort.findByFilters(instructorId, cat, lev, sta);
    }

    @Override
    @Transactional(readOnly = true)
    public Course getCourseById(UUID id) {
        log.info("GrowUp-Log: CourseService - Buscando curso ID: {}", id);
        return coursePersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Course createCourse(Course course, UUID instructorId) {
        log.info("GrowUp-Log: CourseService - Creando curso: {} por instructor: {}", course.getName(), instructorId);

        var instructorInfo = instructorLookupPort.findInstructorInfoById(instructorId);
        if (instructorInfo == null) {
            throw new ResourceNotFoundException("Instructor no encontrado con ID: " + instructorId);
        }

        log.info("GrowUp-Log: CourseService - Instructor encontrado: {}", instructorInfo.getName());
        course.setInstructorId(instructorId);
        return coursePersistencePort.save(course);
    }

    @Override
    @Transactional
    public Course updateCourse(UUID id, Course course, UUID currentUserId) {
        log.info("GrowUp-Log: CourseService - Actualizando curso: {}", id);
        Course existing = getCourseById(id);

        if (!existing.getInstructorId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para editar este curso");
        }

        if (course.getName() != null) {
            existing.setName(course.getName());
        }
        if (course.getDescription() != null) {
            existing.setDescription(course.getDescription());
        }
        if (course.getImageUrl() != null) {
            existing.setImageUrl(course.getImageUrl());
        }
        if (course.getCategory() != null) {
            existing.setCategory(course.getCategory());
        }
        if (course.getLevel() != null) {
            existing.setLevel(course.getLevel());
        }
        if (course.getPrice() != null) {
            existing.setPrice(course.getPrice());
        }
        if (course.getDuration() != null) {
            existing.setDuration(course.getDuration());
        }
        if (course.getPublicationStatus() != null) {
            existing.setPublicationStatus(course.getPublicationStatus());
        }
        if (course.getStartDate() != null) {
            existing.setStartDate(course.getStartDate());
        }
        if (course.getEndDate() != null) {
            existing.setEndDate(course.getEndDate());
        }
        if (course.getSyllabus() != null) {
            existing.setSyllabus(course.getSyllabus());
        }

        return coursePersistencePort.save(existing);
    }

    @Override
    @Transactional
    public void deleteCourse(UUID id, UUID currentUserId) {
        log.info("GrowUp-Log: CourseService - Eliminando curso: {}", id);
        Course existing = getCourseById(id);

        if (!existing.getInstructorId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este curso");
        }

        coursePersistencePort.delete(id);
    }

    @Override
    @Transactional
    public Course updateCoursePrice(UUID id, Double price, UUID currentUserId) {
        log.info("GrowUp-Log: CourseService - Actualizando precio del curso: {} a {} por usuario: {}", id, price, currentUserId);
        Course existing = getCourseById(id);

        existing.setPrice(price);
        return coursePersistencePort.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByInstructor(UUID instructorId) {
        log.info("GrowUp-Log: CourseService - Listando cursos del instructor: {}", instructorId);
        return coursePersistencePort.findByInstructorId(instructorId);
    }
}
