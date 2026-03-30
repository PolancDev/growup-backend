package com.growup.enrollment.domain.port.in;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Puerto de Entrada para los casos de uso del Dashboard.
 */
public interface DashboardInPort {
    Map<String, Object> getDashboardStats(UUID instructorId);

    Map<String, Object> getAnalyticsSummary(UUID instructorId);

    List<Map<String, Object>> getRevenueTrends(UUID instructorId, Integer months);

    List<Map<String, Object>> getCoursePerformance(UUID instructorId);
}