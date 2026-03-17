package com.growup.backend.infrastructure.adapter.web;

import com.growup.backend.api.EstudianteApiDelegate;
import com.growup.backend.domain.port.in.EnrollmentInPort;
import com.growup.backend.domain.port.in.NotificationInPort;
import com.growup.backend.domain.port.in.ReviewInPort;
import com.growup.backend.domain.port.in.UserInPort;
import com.growup.backend.infrastructure.adapter.web.mapper.EnrollmentWebMapper;
import com.growup.backend.infrastructure.adapter.web.mapper.NotificationWebMapper;
import com.growup.backend.infrastructure.adapter.web.mapper.ReviewWebMapper;
import com.growup.backend.model.EnrolledCourse;
import com.growup.backend.model.Notification;
import com.growup.backend.model.Review;
import com.growup.backend.model.StudentStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador Web para Estudiante.
 * Implementa EstudianteApiDelegate delegando en los puertos de entrada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EstudianteWebAdapter implements EstudianteApiDelegate {

    private final EnrollmentInPort enrollmentInPort;
    private final NotificationInPort notificationInPort;
    private final ReviewInPort reviewInPort;
    private final UserInPort userInPort;
    private final EnrollmentWebMapper enrollmentMapper;
    private final NotificationWebMapper notificationMapper;
    private final ReviewWebMapper reviewMapper;

    private UUID getCurrentStudentId() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInPort.getUserByEmail(currentEmail).getId();
    }

    @Override
    public ResponseEntity<Review> coursesIdReviewsPost(UUID id, Review reviewDto) {
        log.info("GrowUp-Log: EstudianteWebAdapter - Creando reseña para curso: {}", id);

        var studentId = getCurrentStudentId();
        var student = userInPort.getUserById(studentId);

        // Aseguramos que el reviewDto tenga el courseId y studentId correctos
        reviewDto.setCourseId(id);
        reviewDto.setStudentId(studentId);
        reviewDto.setStudentName(student.getName());

        var domainReview = reviewMapper.toDomain(reviewDto);
        var created = reviewInPort.createReview(domainReview);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toDto(created));
    }

    @Override
    public ResponseEntity<EnrolledCourse> coursesIdEnrollPost(UUID id) {
        var studentId = getCurrentStudentId();
        var enrollment = enrollmentInPort.enrollStudent(studentId, id);
        return ResponseEntity.ok(enrollmentMapper.toDto(enrollment));
    }

    @Override
    public ResponseEntity<Boolean> coursesIdEnrolledGet(UUID id) {
        var studentId = getCurrentStudentId();
        var isEnrolled = enrollmentInPort.isStudentEnrolled(studentId, id);
        return ResponseEntity.ok(isEnrolled);
    }

    @Override
    public ResponseEntity<List<EnrolledCourse>> studentEnrollmentsGet() {
        var studentId = getCurrentStudentId();
        var enrollments = enrollmentInPort.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<Notification>> studentNotificationsGet(Boolean unreadOnly) {
        var studentId = getCurrentStudentId();
        var notifications = notificationInPort.getNotificationsByUser(studentId);
        if (Boolean.TRUE.equals(unreadOnly)) {
            // Filtrar en memoria para mock
            notifications = notifications.stream().filter(n -> !n.getRead()).collect(Collectors.toList());
        }
        return ResponseEntity.ok(notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Void> studentNotificationsIdReadPut(UUID id) {
        notificationInPort.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<StudentStats> studentStatsGet() {
        var studentId = getCurrentStudentId();
        var stats = enrollmentInPort.getStudentStats(studentId);
        return ResponseEntity.ok(enrollmentMapper.toStatsDto(stats));
    }
}
