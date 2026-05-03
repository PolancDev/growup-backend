package com.growup.enrollment.infrastructure.config;

import com.growup.enrollment.application.service.ActivityService;
import com.growup.enrollment.application.service.DashboardService;
import com.growup.enrollment.application.service.EnrollmentService;
import com.growup.enrollment.application.service.ReviewService;
import com.growup.enrollment.domain.port.in.ActivityInPort;
import com.growup.enrollment.domain.port.in.DashboardInPort;
import com.growup.enrollment.domain.port.in.EnrollmentInPort;
import com.growup.enrollment.domain.port.in.ReviewInPort;
import com.growup.enrollment.domain.port.out.ActivityPersistencePort;
import com.growup.enrollment.domain.port.out.EnrollmentPersistencePort;
import com.growup.enrollment.domain.port.out.ReviewPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración manual de beans de la capa de aplicación.
 * Sigue la Arquitectura Hexagonal: la infraestructura declara los beans,
 * la aplicación solo define la lógica.
 */
@Configuration
public class AppConfig {

    @Bean
    public EnrollmentInPort enrollmentService(
            EnrollmentPersistencePort enrollmentPersistencePort) {
        return new EnrollmentService(enrollmentPersistencePort);
    }

    @Bean
    public ActivityInPort activityService(
            ActivityPersistencePort activityPersistencePort) {
        return new ActivityService(activityPersistencePort);
    }

    @Bean
    public ReviewInPort reviewService(
            ReviewPersistencePort reviewPersistencePort) {
        return new ReviewService(reviewPersistencePort);
    }

    @Bean
    public DashboardInPort dashboardService(
            EnrollmentPersistencePort enrollmentPersistencePort) {
        return new DashboardService(enrollmentPersistencePort);
    }
}
