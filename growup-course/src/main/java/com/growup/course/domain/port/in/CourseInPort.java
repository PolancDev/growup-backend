package com.growup.course.domain.port.in;

import com.growup.course.domain.model.Course;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de Entrada para los casos de uso de Cursos.
 */
public interface CourseInPort {
    List<Course> getAllCourses(UUID instructorId, String category, String level, String status);

    Course getCourseById(UUID id);

    Course createCourse(Course course, UUID instructorId);

    Course updateCourse(UUID id, Course course);

    void deleteCourse(UUID id);

    List<Course> getCoursesByInstructor(UUID instructorId);
}