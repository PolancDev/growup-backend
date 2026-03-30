package com.growup.enrollment.application.service;

import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import com.growup.enrollment.domain.model.Enrollment;
import com.growup.enrollment.domain.model.StudentStats;
import com.growup.enrollment.domain.port.in.EnrollmentInPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService implements EnrollmentInPort {

    // Placeholder: Estas dependencias deberían inyectarse desde auth y course
    // private final EnrollmentPersistencePort enrollmentPersistencePort;
    // private final UserPersistencePort userPersistencePort;
    // private final CoursePersistencePort coursePersistencePort;

    @Override
    public Enrollment enrollStudent(UUID studentId, UUID courseId) {
        log.info("GrowUp-Log: EnrollmentService - Inscribiendo estudiante {} en curso {}", studentId, courseId);

        // Placeholder: Validar estudiante y curso
        // userPersistencePort.findById(studentId).orElseThrow(...);
        // coursePersistencePort.findById(courseId).orElseThrow(...);

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .progress(0)
                .enrollmentStatus("NOT_STARTED")
                .lastAccessDate(OffsetDateTime.now())
                .build();

        return enrollment;
        // return enrollmentPersistencePort.save(enrollment);
    }

    @Override
    public List<Enrollment> getStudentEnrollments(UUID studentId) {
        log.info("GrowUp-Log: EnrollmentService - Obteniendo inscripciones para estudiante {}", studentId);
        return List.of();
        // return enrollmentPersistencePort.findByStudentId(studentId);
    }

    @Override
    public Enrollment updateProgress(UUID enrollmentId, Integer progress, UUID nextLessonId) {
        log.info("GrowUp-Log: EnrollmentService - Actualizando progreso: {}%", progress);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setProgress(progress);
        enrollment.setNextLessonId(nextLessonId);
        enrollment.setLastAccessDate(OffsetDateTime.now());

        if (progress >= 100) {
            enrollment.setEnrollmentStatus("COMPLETED");
        }

        return enrollment;
        // return enrollmentPersistencePort.save(enrollment);
    }

    @Override
    public StudentStats getStudentStats(UUID studentId) {
        log.info("GrowUp-Log: EnrollmentService - Calculando estadísticas para estudiante {}", studentId);
        
        return StudentStats.builder()
                .activeCoursesCount(0)
                .completedCoursesCount(0)
                .totalHoursLearning(BigDecimal.ZERO)
                .averageScore(BigDecimal.ZERO)
                .learningStreakDays(1)
                .certificatesEarned(0)
                .build();
    }

    @Override
    public boolean isStudentEnrolled(UUID studentId, UUID courseId) {
        return false;
        // return enrollmentPersistencePort.existsByStudentIdAndCourseId(studentId, courseId);
    }
}