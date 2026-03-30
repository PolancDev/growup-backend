package com.growup.enrollment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modelo de dominio para las estadísticas de un estudiante.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStats {
    private Integer activeCoursesCount;
    private Integer completedCoursesCount;
    private BigDecimal totalHoursLearning;
    private BigDecimal averageScore;
    private Integer learningStreakDays;
    private Integer certificatesEarned;
}