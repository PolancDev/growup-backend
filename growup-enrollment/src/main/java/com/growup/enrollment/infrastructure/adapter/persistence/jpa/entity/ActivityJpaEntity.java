package com.growup.enrollment.infrastructure.adapter.persistence.jpa.entity;

import com.growup.common.domain.model.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA para el registro de actividades.
 */
@Entity
@Table(name = "activities")
@SQLDelete(sql = "UPDATE activities SET deleted_at = NOW() WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private OffsetDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (time == null) {
            time = OffsetDateTime.now();
        }
        updatedAt = OffsetDateTime.now();
        deletedAt = null;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Version
    @Builder.Default
    private Long version = 0L;
}