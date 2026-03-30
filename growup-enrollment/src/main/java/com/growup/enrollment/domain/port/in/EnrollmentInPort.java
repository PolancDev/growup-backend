package com.growup.enrollment.domain.port.in;

import com.growup.enrollment.domain.model.Enrollment;
import com.growup.enrollment.domain.model.StudentStats;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de Entrada para los casos de uso de Inscripciones.
 */
public interface EnrollmentInPort {
    Enrollment enrollStudent(UUID studentId, UUID courseId);

    List<Enrollment> getStudentEnrollments(UUID studentId);

    Enrollment updateProgress(UUID enrollmentId, Integer progress, UUID nextLessonId);

    StudentStats getStudentStats(UUID studentId);

    boolean isStudentEnrolled(UUID studentId, UUID courseId);
}