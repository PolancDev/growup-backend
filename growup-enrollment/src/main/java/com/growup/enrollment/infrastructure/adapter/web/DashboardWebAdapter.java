package com.growup.enrollment.infrastructure.adapter.web;

import com.growup.enrollment.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador Web para Dashboard.
 * Endpoints REST para estadísticas y dashboard.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard and statistics endpoints")
public class DashboardWebAdapter {

    private final DashboardService dashboardService;

    @GetMapping("/teachers/{teacherId}/dashboard")
    @Operation(summary = "Get teacher dashboard", description = "Get dashboard stats for a teacher")
    public ResponseEntity<Map<String, Object>> getTeacherDashboard(
            @Parameter(description = "Teacher UUID") @PathVariable UUID teacherId) {
        log.info("GrowUp-Log: Get dashboard for teacher: {}", teacherId);
        
        var stats = dashboardService.getDashboardStats(teacherId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/teachers/{teacherId}/analytics")
    @Operation(summary = "Get teacher analytics", description = "Get analytics summary for a teacher")
    public ResponseEntity<Map<String, Object>> getTeacherAnalytics(
            @Parameter(description = "Teacher UUID") @PathVariable UUID teacherId) {
        log.info("GrowUp-Log: Get analytics for teacher: {}", teacherId);
        
        var analytics = dashboardService.getAnalyticsSummary(teacherId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/teachers/{teacherId}/revenue")
    @Operation(summary = "Get revenue trends", description = "Get revenue trends for a teacher")
    public ResponseEntity<List<Map<String, Object>>> getRevenueTrends(
            @Parameter(description = "Teacher UUID") @PathVariable UUID teacherId,
            @Parameter(description = "Number of months") @RequestParam(defaultValue = "12") Integer months) {
        log.info("GrowUp-Log: Get revenue trends for teacher: {}", teacherId);
        
        var trends = dashboardService.getRevenueTrends(teacherId, months);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/teachers/{teacherId}/performance")
    @Operation(summary = "Get course performance", description = "Get performance stats for teacher's courses")
    public ResponseEntity<List<Map<String, Object>>> getCoursePerformance(
            @Parameter(description = "Teacher UUID") @PathVariable UUID teacherId) {
        log.info("GrowUp-Log: Get course performance for teacher: {}", teacherId);
        
        var performance = dashboardService.getCoursePerformance(teacherId);
        return ResponseEntity.ok(performance);
    }
}
