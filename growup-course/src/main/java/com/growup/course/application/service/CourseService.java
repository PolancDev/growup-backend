package com.growup.course.application.service;

import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import com.growup.course.domain.model.Course;
import com.growup.course.domain.port.in.CourseInPort;
import com.growup.course.domain.port.out.CoursePersistencePort;
import com.growup.course.domain.port.out.InstructorLookupPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public List<Course> getAllCourses(UUID instructorId, String category, String level, String status) {
        String cat = (category == null || category.trim().isEmpty()) ? null : category;
        String lev = (level == null || level.trim().isEmpty()) ? null : level;
        String sta = (status == null || status.trim().isEmpty()) ? null : status;

        log.info("GrowUp-Log: CourseService - Listando cursos con filtros: {}, {}, {}",
                cat, lev, sta);
        return coursePersistencePort.findByFilters(instructorId, cat, lev, sta);
    }

    @Override
    public Course getCourseById(UUID id) {
        log.info("GrowUp-Log: CourseService - Buscando curso ID: {}", id);
        return coursePersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
    }

    @Override
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
    public Course updateCourse(UUID id, Course course) {
        log.info("GrowUp-Log: CourseService - Actualizando curso: {}", id);
        Course existing = getCourseById(id);

        if (course.getName() != null && !course.getName().equals(existing.getName())) {
            existing.setName(course.getName());
        }
        if (course.getDescription() != null && !course.getDescription().equals(existing.getDescription())) {
            existing.setDescription(course.getDescription());
        }
        if (course.getImageUrl() != null && !course.getImageUrl().equals(existing.getImageUrl())) {
            existing.setImageUrl(course.getImageUrl());
        }
        if (course.getCategory() != null && !course.getCategory().equals(existing.getCategory())) {
            existing.setCategory(course.getCategory());
        }
        if (course.getLevel() != null && !course.getLevel().equals(existing.getLevel())) {
            existing.setLevel(course.getLevel());
        }
        if (course.getPrice() != null && !course.getPrice().equals(existing.getPrice())) {
            existing.setPrice(course.getPrice());
        }
        if (course.getDuration() != null && !course.getDuration().equals(existing.getDuration())) {
            existing.setDuration(course.getDuration());
        }
        if (course.getPublicationStatus() != null
                && !course.getPublicationStatus().equals(existing.getPublicationStatus())) {
            existing.setPublicationStatus(course.getPublicationStatus());
        }
        if (course.getStartDate() != null && !course.getStartDate().equals(existing.getStartDate())) {
            existing.setStartDate(course.getStartDate());
        }
        if (course.getEndDate() != null && !course.getEndDate().equals(existing.getEndDate())) {
            existing.setEndDate(course.getEndDate());
        }
        if (course.getSyllabus() != null && !course.getSyllabus().isEmpty()
                && !course.getSyllabus().equals(existing.getSyllabus())) {

            course.getSyllabus().forEach(newModule -> {
                if (newModule.getId() != null) {
                    existing.getSyllabus().stream()
                            .filter(m -> newModule.getId().equals(m.getId()))
                            .findFirst()
                            .ifPresent(existingModule -> newModule.setVersion(existingModule.getVersion()));
                } else if (newModule.getVersion() == null) {
                    newModule.setVersion(0L);
                }

                if (newModule.getTopics() != null) {
                    newModule.getTopics().forEach(newTopic -> {
                        if (newTopic.getId() != null) {
                            existing.getSyllabus().stream()
                                    .filter(m -> m.getTopics() != null)
                                    .flatMap(m -> m.getTopics().stream())
                                    .filter(t -> newTopic.getId().equals(t.getId()))
                                    .findFirst()
                                    .ifPresent(existingTopic -> newTopic.setVersion(existingTopic.getVersion()));
                        } else if (newTopic.getVersion() == null) {
                            newTopic.setVersion(0L);
                        }
                    });
                }
            });

            existing.setSyllabus(course.getSyllabus());
        }

        return coursePersistencePort.save(existing);
    }

    @Override
    public void deleteCourse(UUID id) {
        log.info("GrowUp-Log: CourseService - Eliminando curso: {}", id);
        coursePersistencePort.delete(id);
    }

    @Override
    public List<Course> getCoursesByInstructor(UUID instructorId) {
        log.info("GrowUp-Log: CourseService - Listando cursos del instructor: {}", instructorId);
        List<Course> courses = coursePersistencePort.findByInstructorId(instructorId);
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("El instructor con ID: " + instructorId + " no tiene cursos");
        }
        return courses;
    }
}