package com.growup.enrollment.application.service;

import com.growup.common.domain.model.enums.EnrollmentStatus;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import com.growup.enrollment.domain.model.Enrollment;
import com.growup.enrollment.domain.model.StudentStats;
import com.growup.enrollment.domain.port.in.EnrollmentInPort;
import com.growup.enrollment.domain.port.out.EnrollmentPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación para Inscripciones.
 * 
 * NOTA: Este servicio tiene dependencias con los puertos de Course y User
 * que están en otros módulos. En una arquitectura multi-módulo pura,
 * estas dependencias se resolverían mediante comunicación por eventos o clientes REST.
 * NO usa anotaciones de Spring (@Service) - se configura manualmente en AppConfig.
 */
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService implements EnrollmentInPort {

    private final EnrollmentPersistencePort enrollmentPersistencePort;

    @Override
    public Enrollment enrollStudent(UUID studentId, UUID courseId) {
        log.info("GrowUp-Log: EnrollmentService - Inscribiendo estudiante {} en curso {}", studentId, courseId);

        // Validar si ya está inscrito
        if (enrollmentPersistencePort.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("El estudiante ya está inscrito en este curso");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .progress(0)
                .enrollmentStatus(EnrollmentStatus.NOT_STARTED)
                .lastAccessDate(OffsetDateTime.now())
                .build();

        return enrollmentPersistencePort.save(enrollment);
    }

    @Override
    public List<Enrollment> getStudentEnrollments(UUID studentId) {
        log.info("GrowUp-Log: EnrollmentService - Obteniendo inscripciones para estudiante {}", studentId);
        return enrollmentPersistencePort.findByStudentId(studentId);
    }

    @Override
    public Enrollment updateProgress(UUID enrollmentId, Integer progress, UUID nextLessonId) {
        log.info("GrowUp-Log: EnrollmentService - Actualizando progreso: {}%", progress);

        Enrollment enrollment = enrollmentPersistencePort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + enrollmentId));

        enrollment.setProgress(progress);
        enrollment.setLastAccessDate(OffsetDateTime.now());

        if (nextLessonId != null) {
            enrollment.setNextLessonId(nextLessonId);
        }

        if (progress >= 100) {
            enrollment.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
        } else if (progress > 0) {
            enrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        }

        return enrollmentPersistencePort.save(enrollment);
    }

    @Override
    public StudentStats getStudentStats(UUID studentId) {
        log.info("GrowUp-Log: EnrollmentService - Calculando estadísticas para estudiante {}", studentId);
        
        List<Enrollment> enrollments = enrollmentPersistencePort.findByStudentId(studentId);
        
        long activeCourses = enrollments.stream()
                .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.ACTIVE)
                .count();
        
        long completedCourses = enrollments.stream()
                .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.COMPLETED)
                .count();
        
        // Cálculo simplificado - en producción se obtendría de los cursos
        BigDecimal totalHours = BigDecimal.valueOf(
                enrollments.stream()
                        .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.COMPLETED)
                        .count() * 10); // 10 horas por curso completado (ejemplo)
        
        // Promedio de progreso
        BigDecimal averageScore = enrollments.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(enrollments.stream()
                        .mapToInt(Enrollment::getProgress)
                        .average()
                        .orElse(0));
        
        return StudentStats.builder()
                .activeCoursesCount((int) activeCourses)
                .completedCoursesCount((int) completedCourses)
                .totalHoursLearning(totalHours)
                .averageScore(averageScore)
                .learningStreakDays(1) // Requiere lógica adicional para calcular racha
                .certificatesEarned((int) completedCourses)
                .build();
    }

    @Override
    public boolean isStudentEnrolled(UUID studentId, UUID courseId) {
        return enrollmentPersistencePort.existsByStudentIdAndCourseId(studentId, courseId);
    }
}