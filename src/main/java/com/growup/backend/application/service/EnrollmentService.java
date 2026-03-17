package com.growup.backend.application.service;

import com.growup.backend.domain.port.in.EnrollmentInPort;
import com.growup.backend.domain.port.out.CoursePersistencePort;
import com.growup.backend.domain.port.out.EnrollmentPersistencePort;
import com.growup.backend.domain.port.out.UserPersistencePort;
import com.growup.backend.domain.model.Course;
import com.growup.backend.domain.model.Enrollment;
import com.growup.backend.domain.model.StudentStats;
import com.growup.backend.domain.model.User;
import com.growup.backend.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación para Inscripciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService implements EnrollmentInPort {

        private final EnrollmentPersistencePort enrollmentPersistencePort;
        private final UserPersistencePort userPersistencePort;
        private final CoursePersistencePort coursePersistencePort;

        @Override
        public Enrollment enrollStudent(UUID studentId, UUID courseId) {
                log.info("GrowUp-Log: EnrollmentService - Inscribiendo estudiante {} en curso {}", studentId, courseId);

                User student = userPersistencePort.findById(studentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

                Course course = coursePersistencePort.findById(courseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

                if (enrollmentPersistencePort.existsByStudentIdAndCourseId(studentId, courseId)) {
                        throw new IllegalStateException("El estudiante ya está inscrito en este curso");
                }

                Enrollment enrollment = Enrollment.builder()
                                .student(student)
                                .course(course)
                                .progress(0)
                                .enrollmentStatus("NOT_STARTED")
                                .lastAccessDate(OffsetDateTime.now())
                                .build();

                Enrollment saved = enrollmentPersistencePort.save(enrollment);

                // Lógica de negocio: Incrementar contador en el curso
                course.setEnrolledCount(course.getEnrolledCount() + 1);
                coursePersistencePort.save(course);

                return saved;
        }

        @Override
        public List<Enrollment> getStudentEnrollments(UUID studentId) {
                return enrollmentPersistencePort.findByStudentId(studentId);
        }

        @Override
        public Enrollment updateProgress(UUID enrollmentId, Integer progress, UUID nextLessonId) {
                log.info("GrowUp-Log: EnrollmentService - Actualizando progreso: {}%", progress);
                Enrollment enrollment = enrollmentPersistencePort.findById(enrollmentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

                enrollment.setProgress(progress);
                enrollment.setNextLessonId(nextLessonId);
                enrollment.setLastAccessDate(OffsetDateTime.now());

                if (progress >= 100) {
                        enrollment.setEnrollmentStatus("COMPLETED");
                }

                return enrollmentPersistencePort.save(enrollment);
        }

        @Override
        public StudentStats getStudentStats(UUID studentId) {
                log.info("GrowUp-Log: EnrollmentService - Calculando estadísticas para estudiante {}", studentId);
                List<Enrollment> enrollments = enrollmentPersistencePort.findByStudentId(studentId);

                return StudentStats.builder()
                                .activeCoursesCount((int) enrollments.stream()
                                                .filter(e -> "ACTIVE".equals(e.getEnrollmentStatus()))
                                                .count())
                                .completedCoursesCount((int) enrollments.stream()
                                                .filter(e -> "COMPLETED".equals(e.getEnrollmentStatus()))
                                                .count())
                                .totalHoursLearning(java.math.BigDecimal.ZERO) // Simplificado para el ejemplo
                                .averageScore(java.math.BigDecimal.ZERO)
                                .learningStreakDays(1)
                                .certificatesEarned((int) enrollments.stream()
                                                .filter(e -> "COMPLETED".equals(e.getEnrollmentStatus()))
                                                .count())
                                .build();
        }

        @Override
        public boolean isStudentEnrolled(UUID studentId, UUID courseId) {
                return enrollmentPersistencePort.existsByStudentIdAndCourseId(studentId, courseId);
        }
}
