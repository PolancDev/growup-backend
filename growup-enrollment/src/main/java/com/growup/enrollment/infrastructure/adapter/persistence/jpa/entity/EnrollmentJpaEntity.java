package com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity;

import com.growup.common.domain.model.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la persistencia de inscripciones.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "course_id" }))
@SQLDelete(sql = "UPDATE enrollments SET deleted_at = NOW() WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Builder.Default
    @Column(nullable = false)
    private Integer progress = 0;

    @Column(name = "last_access_date")
    private OffsetDateTime lastAccessDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status", nullable = false)
    @Builder.Default
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.NOT_STARTED;

    @Column(name = "next_lesson_id")
    private UUID nextLessonId;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        deletedAt = null;
        if (lastAccessDate == null)
            lastAccessDate = OffsetDateTime.now();
        if (progress == null)
            progress = 0;
        if (enrollmentStatus == null)
            enrollmentStatus = EnrollmentStatus.NOT_STARTED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Version
    @Builder.Default
    private Long version = 0L;
}