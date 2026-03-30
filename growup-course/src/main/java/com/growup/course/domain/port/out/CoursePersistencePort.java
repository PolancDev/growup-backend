package com.growup.course.domain.port.out;

import com.growup.course.domain.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de Salida para la persistencia de Cursos.
 */
public interface CoursePersistencePort {
    Optional<Course> findById(UUID id);

    List<Course> findAll();

    List<Course> findByFilters(UUID instructorId, String category, String level, String status);

    List<Course> findByInstructorId(UUID instructorId);

    Course save(Course course);

    void delete(UUID id);

    List<Course> findByCategory(String category);

    void deleteById(UUID id);
}