package com.growup.course.infrastructure.adapter.persistence.jpa.repository;

import com.growup.course.infrastructure.adapter.persistence.jpa.entity.CourseModuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad CourseModuleJpaEntity.
 */
@Repository
public interface CourseModuleJpaRepository extends JpaRepository<CourseModuleJpaEntity, UUID> {

    @Query("SELECT m FROM CourseModuleJpaEntity m WHERE m.course.id = :courseId")
    List<CourseModuleJpaEntity> findByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT m FROM CourseModuleJpaEntity m LEFT JOIN FETCH m.topics WHERE m.course.id = :courseId")
    List<CourseModuleJpaEntity> findByCourseIdWithTopics(@Param("courseId") UUID courseId);

    @Query("DELETE FROM CourseModuleJpaEntity m WHERE m.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") UUID courseId);
}
