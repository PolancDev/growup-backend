package com.growup.course.infrastructure.adapter.persistence.jpa.entity;

import com.growup.common.domain.model.enums.CourseLevel;
import com.growup.common.domain.model.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA para la persistencia de cursos.
 */
@Entity
@Table(name = "courses")
@SQLDelete(sql = "UPDATE courses SET deleted_at = NOW() WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(nullable = false)
    private Double price;

    private BigDecimal duration;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_status", nullable = false)
    private CourseStatus publicationStatus;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "instructor_id", nullable = false)
    private UUID instructorId;

    @Builder.Default
    @Column(name = "enrolled_count")
    private Integer enrolledCount = 0;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("order ASC")
    private List<CourseModuleJpaEntity> modules = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        deletedAt = null;
        if (enrolledCount == null)
            enrolledCount = 0;
        if (publicationStatus == null)
            publicationStatus = CourseStatus.BORRADOR;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Version
    @Builder.Default
    private Long version = 0L;
}