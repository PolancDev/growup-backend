package com.growup.enrollment.application.service;

import com.growup.enrollment.domain.port.in.DashboardInPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService implements DashboardInPort {

    @Override
    public Map<String, Object> getDashboardStats(UUID instructorId) {
        log.info("GrowUp-Log: DashboardService - Calculando estadísticas para instructor: {}", instructorId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", 0);
        stats.put("activeCourses", 0);
        stats.put("averageRating", BigDecimal.ZERO);
        stats.put("monthlyRevenue", BigDecimal.ZERO);
        stats.put("studentsGrowth", BigDecimal.ZERO);
        stats.put("revenueGrowth", BigDecimal.ZERO);

        return stats;
    }

    @Override
    public Map<String, Object> getAnalyticsSummary(UUID instructorId) {
        log.info("GrowUp-Log: DashboardService - Calculando resumen de analíticas para instructor: {}", instructorId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("activeCourses", 0);
        summary.put("totalStudents", 0);
        summary.put("totalRevenue", BigDecimal.ZERO);
        summary.put("averageRating", BigDecimal.ZERO);

        return summary;
    }

    @Override
    public List<Map<String, Object>> getRevenueTrends(UUID instructorId, Integer months) {
        int monthsToFetch = months != null ? months : 12;
        log.info("GrowUp-Log: DashboardService - Calculando tendencias de ingresos ({} meses) para instructor: {}",
                monthsToFetch, instructorId);

        return List.of();
    }

    @Override
    public List<Map<String, Object>> getCoursePerformance(UUID instructorId) {
        log.info("GrowUp-Log: DashboardService - Calculando rendimiento por curso para instructor: {}",
                instructorId);

        return List.of();
    }
}