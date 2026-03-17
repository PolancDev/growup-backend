package com.growup.backend.infrastructure.adapter.web.mapper;

import com.growup.backend.domain.model.Course;
import com.growup.backend.domain.model.CourseModule;
import com.growup.backend.domain.model.Topic;
import com.growup.backend.model.CourseItem;
import com.growup.backend.model.Syllabus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual para convertir entre el modelo de dominio Course y los DTOs de
 * OpenAPI.
 */
@Component
@RequiredArgsConstructor
public class CourseWebMapper {

    private final UserWebMapper userMapper;

    /** Dominio Course -> DTO Course (detalles completos) */
    public com.growup.backend.model.Course toDto(Course domain) {
        if (domain == null)
            return null;

        com.growup.backend.model.Course dto = new com.growup.backend.model.Course();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setImageUrl(domain.getImageUrl());
        dto.setCategory(domain.getCategory());
        dto.setLevel(mapToModelLevel(domain.getLevel()));
        dto.setPrice(domain.getPrice());
        dto.setDuration(domain.getDuration());
        dto.setStartDate(domain.getStartDate());
        dto.setEndDate(domain.getEndDate());
        dto.setPublicationStatus(mapToModelStatus(domain.getPublicationStatus()));
        dto.setCreatedAt(domain.getCreatedAt());
        dto.setUpdatedAt(domain.getUpdatedAt());
        dto.setInstructor(userMapper.toInstructorDto(domain.getInstructor()));
        dto.setSyllabus(mapModuleListToDto(domain.getSyllabus()));
        dto.setEnrolledCount(domain.getEnrolledCount());
        dto.setVersion(domain.getVersion());
        return dto;
    }

    /** DTO Course -> Dominio Course */
    public Course toDomain(com.growup.backend.model.Course dto) {
        if (dto == null)
            return null;

        Course domain = new Course();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        domain.setDescription(dto.getDescription());
        domain.setImageUrl(dto.getImageUrl());
        domain.setCategory(dto.getCategory());
        domain.setLevel(mapToDomainLevel(dto.getLevel()));
        domain.setPrice(dto.getPrice());
        domain.setDuration(dto.getDuration());
        domain.setStartDate(dto.getStartDate());
        domain.setEndDate(dto.getEndDate());
        domain.setPublicationStatus(mapToDomainStatus(dto.getPublicationStatus()));
        domain.setCreatedAt(dto.getCreatedAt());
        domain.setUpdatedAt(dto.getUpdatedAt());
        domain.setSyllabus(mapModuleListToDomain(dto.getSyllabus()));
        domain.setVersion(dto.getVersion());
        return domain;
    }

    /** Dominio Course -> DTO CourseItem (para los listados del catálogo) */
    public CourseItem toCourseItemDto(Course domain) {
        if (domain == null)
            return null;

        CourseItem dto = new CourseItem();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setImageUrl(domain.getImageUrl());
        dto.setCategory(domain.getCategory());
        dto.setLevel(mapToModelLevel(domain.getLevel()));
        dto.setPrice(domain.getPrice());
        dto.setDuration(domain.getDuration());
        dto.setStartDate(domain.getStartDate());
        dto.setEndDate(domain.getEndDate());
        dto.setPublicationStatus(mapToModelStatus(domain.getPublicationStatus()));
        dto.setCreatedAt(domain.getCreatedAt());
        dto.setUpdatedAt(domain.getUpdatedAt());
        dto.setInstructor(userMapper.toInstructorDto(domain.getInstructor()));
        dto.setSyllabus(mapModuleListToDto(domain.getSyllabus()));
        dto.setStudents(domain.getEnrolledCount());
        return dto;
    }

    // --- Módulos (Syllabus en el DTO) ---

    public Syllabus toModuleDto(CourseModule domain) {
        if (domain == null)
            return null;

        Syllabus dto = new Syllabus();
        dto.setId(domain.getId());
        dto.setTitle(domain.getTitle());
        dto.setDescription(domain.getDescription());
        dto.setOrder(domain.getOrder());
        dto.setTopics(mapTopicListToDto(domain.getTopics()));
        return dto;
    }

    public CourseModule toModuleDomain(Syllabus dto) {
        if (dto == null)
            return null;

        CourseModule domain = new CourseModule();
        domain.setId(dto.getId());
        domain.setTitle(dto.getTitle());
        domain.setDescription(dto.getDescription());
        domain.setOrder(dto.getOrder());
        domain.setTopics(mapTopicListToDomain(dto.getTopics()));
        return domain;
    }

    // --- Temas ---

    public com.growup.backend.model.Topic toTopicDto(Topic domain) {
        if (domain == null)
            return null;

        com.growup.backend.model.Topic dto = new com.growup.backend.model.Topic();
        dto.setTitle(domain.getName());
        dto.setDuration(mapDurationToInt(domain.getDuration()));
        return dto;
    }

    public Topic toTopicDomain(com.growup.backend.model.Topic dto) {
        if (dto == null)
            return null;

        Topic domain = new Topic();
        domain.setName(dto.getTitle());
        domain.setDuration(mapDurationToString(dto.getDuration()));
        domain.setIsFree(false);
        return domain;
    }

    // --- Auxiliares ---

    private List<Syllabus> mapModuleListToDto(List<CourseModule> list) {
        if (list == null)
            return new ArrayList<>();
        return list.stream().map(this::toModuleDto).collect(Collectors.toList());
    }

    private List<CourseModule> mapModuleListToDomain(List<Syllabus> list) {
        if (list == null)
            return new ArrayList<>();
        return list.stream().map(this::toModuleDomain).collect(Collectors.toList());
    }

    private List<com.growup.backend.model.Topic> mapTopicListToDto(List<Topic> list) {
        if (list == null)
            return new ArrayList<>();
        return list.stream().map(this::toTopicDto).collect(Collectors.toList());
    }

    private List<Topic> mapTopicListToDomain(List<com.growup.backend.model.Topic> list) {
        if (list == null)
            return new ArrayList<>();
        return list.stream().map(this::toTopicDomain).collect(Collectors.toList());
    }

    // --- Conversiones de tipos ---

    private com.growup.backend.model.CourseLevel mapToModelLevel(String level) {
        if (level == null)
            return null;
        try {
            return com.growup.backend.model.CourseLevel.fromValue(level);
        } catch (Exception e) {
            return null;
        }
    }

    private String mapToDomainLevel(com.growup.backend.model.CourseLevel level) {
        return level != null ? level.getValue() : null;
    }

    private com.growup.backend.model.CourseStatus mapToModelStatus(String status) {
        if (status == null)
            return null;
        try {
            return com.growup.backend.model.CourseStatus.fromValue(status);
        } catch (Exception e) {
            return null;
        }
    }

    private String mapToDomainStatus(com.growup.backend.model.CourseStatus status) {
        return status != null ? status.getValue() : null;
    }

    private Integer mapDurationToInt(String duration) {
        if (duration == null)
            return null;
        try {
            return Integer.parseInt(duration.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private String mapDurationToString(Integer duration) {
        return duration != null ? duration + " hours" : null;
    }
}
