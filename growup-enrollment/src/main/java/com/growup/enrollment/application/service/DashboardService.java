package com.growup.enrollment.application.service;

import com.growup.enrollment.domain.port.in.DashboardInPort;
import com.growup.enrollment.domain.port.out.EnrollmentPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de Aplicación para el Dashboard.
 * Agrega datos de cursos e inscripciones para calcular estadísticas.
 * 
 * NOTA: Este servicio necesita dependencias de los módulos course y auth.
 * En producción, se usarían clientes REST o comunicación por eventos.
 * NO usa anotaciones de Spring (@Service) - se configura manualmente en AppConfig.
 */
@RequiredArgsConstructor
@Slf4j
public class DashboardService implements DashboardInPort {

    private final EnrollmentPersistencePort enrollmentPersistencePort;

    @Override
    public Map<String, Object> getDashboardStats(UUID instructorId) {
        log.info("GrowUp-Log: DashboardService - Calculando estadísticas para instructor: {}", instructorId);

        // Obtener inscripciones reales del instructor
        List<com.growup.enrollment.domain.model.Enrollment> enrollments = 
                enrollmentPersistencePort.findByInstructorId(instructorId);

        long activeCourses = enrollments.stream()
                .filter(e -> e.getEnrollmentStatus() != null)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", enrollments.size());
        stats.put("activeCourses", activeCourses);
        stats.put("averageRating", BigDecimal.ZERO); // Requiere datos de reviews
        stats.put("monthlyRevenue", BigDecimal.ZERO); // Requiere datos de pagos
        stats.put("studentsGrowth", BigDecimal.ZERO);
        stats.put("revenueGrowth", BigDecimal.ZERO);

        return stats;
    }

    @Override
    public Map<String, Object> getAnalyticsSummary(UUID instructorId) {
        log.info("GrowUp-Log: DashboardService - Calculando resumen de analíticas para instructor: {}", instructorId);

        List<com.growup.enrollment.domain.model.Enrollment> enrollments = 
                enrollmentPersistencePort.findByInstructorId(instructorId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("activeCourses", enrollments.size());
        summary.put("totalStudents", enrollments.stream()
                .map(com.growup.enrollment.domain.model.Enrollment::getStudentId)
                .distinct()
                .count());
        summary.put("totalRevenue", BigDecimal.ZERO);
        summary.put("averageRating", BigDecimal.ZERO);

        return summary;
    }

    @Override
    public List<Map<String, Object>> getRevenueTrends(UUID instructorId, Integer months) {
        int monthsToFetch = months != null ? months : 12;
        log.info("GrowUp-Log: DashboardService - Calculando tendencias de ingresos ({} meses) para instructor: {}",
                monthsToFetch, instructorId);

        // Placeholder - requiere integración con módulo de pagos
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getCoursePerformance(UUID instructorId) {
        log.info("GrowUp-Log: DashboardService - Calculando rendimiento por curso para instructor: {}",
                instructorId);

        // Placeholder - requiere integración con módulo de cursos
        return List.of();
    }
}