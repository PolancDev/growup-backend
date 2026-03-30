package com.growup.enrollment.infrastructure.adapter.web;

import com.growup.enrollment.application.service.ActivityService;
import com.growup.enrollment.domain.model.Activity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Actividades.
 * Endpoints REST para gestión de activities.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/student/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Learning activity endpoints")
public class ActivityWebAdapter {

    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Get student activities", description = "Get all activities for a student")
    public ResponseEntity<List<Map<String, Object>>> getStudentActivities(
            @Parameter(description = "Student UUID") @RequestParam UUID studentId) {
        log.info("GrowUp-Log: Get activities for student: {}", studentId);
        
        var activities = activityService.getActivitiesByUser(studentId);
        return ResponseEntity.ok(activities.stream()
                .map(this::mapActivityToDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(summary = "Log activity", description = "Log a new learning activity")
    public ResponseEntity<Map<String, Object>> logActivity(@RequestBody Map<String, Object> activityData) {
        log.info("GrowUp-Log: Log activity: {}", activityData);
        
        UUID studentId = UUID.fromString(activityData.get("studentId").toString());
        String action = (String) activityData.get("action");
        String target = (String) activityData.get("target");
        String type = (String) activityData.get("type");
        
        activityService.logActivity(studentId, type, action, target);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "logged");
        response.put("studentId", studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Map<String, Object> mapActivityToDto(Activity activity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", activity.getId());
        dto.put("userId", activity.getUserId());
        dto.put("action", activity.getAction());
        dto.put("target", activity.getTarget());
        dto.put("time", activity.getTime());
        dto.put("type", activity.getType());
        return dto;
    }
}
