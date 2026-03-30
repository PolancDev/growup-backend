package com.growup.course.infrastructure.adapter.persistence;

import com.growup.common.domain.model.enums.CourseLevel;
import com.growup.common.domain.model.enums.CourseStatus;
import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import com.growup.course.domain.model.Course;
import com.growup.course.domain.port.out.CoursePersistencePort;
import com.growup.course.infrastructure.adapter.persistence.jpa.entity.CourseJpaEntity;
import com.growup.course.infrastructure.adapter.persistence.jpa.repository.CourseJpaRepository;
import com.growup.course.infrastructure.adapter.persistence.mapper.CoursePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para cursos.
 */
@Component
@RequiredArgsConstructor
public class CoursePersistenceAdapter implements CoursePersistencePort {

    private final CourseJpaRepository courseRepository;
    private final CoursePersistenceMapper courseMapper;

    @Override
    public Course save(Course course) {
        var entity = courseMapper.toEntity(course);
        if (entity == null) {
            throw new ResourceNotFoundException("Curso no encontrado en BBDD");
        }
        var savedEntity = courseRepository.save(entity);
        return courseMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Course> findById(UUID id) {
        // Uso findById estándar - la relación syllabus se puede agregar después si es necesario
        return courseRepository.findById(id).map(courseMapper::toDomain);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findByCategory(String category) {
        return courseRepository.findByCategory(category).stream()
                .map(courseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findByInstructorId(UUID instructorId) {
        return courseRepository.findByInstructorId(instructorId).stream()
                .map(courseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        if (id != null) {
            courseRepository.deleteById(id);
        }
    }

    @Override
    public List<Course> findByFilters(UUID instructorId, String category, String level, String status) {
        final CourseLevel levelEnum = (level != null && !level.isBlank()) ? CourseLevel.fromValue(level) : null;
        final CourseStatus statusEnum = (status != null && !status.isBlank()) ? CourseStatus.fromValue(status) : null;

        Specification<CourseJpaEntity> specification = Specification.where(null);

        if (instructorId != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("instructorId"), instructorId));
        }

        if (category != null && !category.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), category));
        }

        if (levelEnum != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("level"), levelEnum));
        }

        if (statusEnum != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("publicationStatus"), statusEnum));
        }

        return courseRepository.findAll(specification).stream()
                .map(courseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (id != null) {
            deleteById(id);
        }
    }
}