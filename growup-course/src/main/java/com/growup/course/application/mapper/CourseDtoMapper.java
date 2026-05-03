package com.growup.course.application.mapper;

import com.growup.course.application.dto.CourseModuleDto;
import com.growup.course.application.dto.CourseRequest;
import com.growup.course.application.dto.CourseResponse;
import com.growup.course.application.dto.TopicDto;
import com.growup.course.domain.model.Course;
import com.growup.course.domain.model.CourseModule;
import com.growup.course.domain.model.Topic;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual entre DTOs de aplicación y modelos de dominio.
 */
@Component
public class CourseDtoMapper {

    public Course toDomain(CourseRequest request) {
        if (request == null) {
            return null;
        }
        Course course = new Course();
        course.setId(request.getId());
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel() != null ? request.getLevel().getValue() : null);
        course.setPrice(request.getPrice());
        course.setDuration(request.getDuration());
        course.setPublicationStatus(request.getPublicationStatus() != null ? request.getPublicationStatus().getValue() : null);
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            course.setImageUrl(URI.create(request.getImageUrl()));
        }
        course.setSyllabus(toDomainModules(request.getSyllabus()));
        return course;
    }

    public CourseResponse toResponse(Course course) {
        if (course == null) {
            return null;
        }
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setDescription(course.getDescription());
        response.setImageUrl(course.getImageUrl() != null ? course.getImageUrl().toString() : null);
        response.setCategory(course.getCategory());
        response.setLevel(com.growup.common.domain.model.enums.CourseLevel.fromValue(course.getLevel()));
        response.setPrice(course.getPrice());
        response.setDuration(course.getDuration());
        response.setStartDate(course.getStartDate());
        response.setEndDate(course.getEndDate());
        response.setPublicationStatus(com.growup.common.domain.model.enums.CourseStatus.fromValue(course.getPublicationStatus()));
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        response.setInstructorId(course.getInstructorId());
        response.setEnrolledCount(course.getEnrolledCount());
        response.setVersion(course.getVersion());
        response.setSyllabus(toResponseModules(course.getSyllabus()));
        return response;
    }

    private List<CourseModule> toDomainModules(List<CourseModuleDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream().map(this::toDomainModule).collect(Collectors.toList());
    }

    private CourseModule toDomainModule(CourseModuleDto dto) {
        if (dto == null) {
            return null;
        }
        CourseModule module = new CourseModule();
        module.setId(dto.getId());
        module.setTitle(dto.getTitle());
        module.setDescription(dto.getDescription());
        module.setOrder(dto.getOrder());
        module.setVersion(dto.getVersion());
        module.setTopics(toDomainTopics(dto.getTopics()));
        return module;
    }

    private List<Topic> toDomainTopics(List<TopicDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream().map(this::toDomainTopic).collect(Collectors.toList());
    }

    private Topic toDomainTopic(TopicDto dto) {
        if (dto == null) {
            return null;
        }
        Topic topic = new Topic();
        topic.setId(dto.getId());
        topic.setName(dto.getTitle());
        topic.setDuration(dto.getDuration());
        topic.setIsFree(dto.getIsFree());
        topic.setVersion(dto.getVersion());
        return topic;
    }

    private List<CourseModuleDto> toResponseModules(List<CourseModule> modules) {
        if (modules == null) {
            return new ArrayList<>();
        }
        return modules.stream().map(this::toResponseModule).collect(Collectors.toList());
    }

    private CourseModuleDto toResponseModule(CourseModule module) {
        if (module == null) {
            return null;
        }
        CourseModuleDto dto = new CourseModuleDto();
        dto.setId(module.getId());
        dto.setTitle(module.getTitle());
        dto.setDescription(module.getDescription());
        dto.setOrder(module.getOrder());
        dto.setVersion(module.getVersion());
        dto.setTopics(toResponseTopics(module.getTopics()));
        return dto;
    }

    private List<TopicDto> toResponseTopics(List<Topic> topics) {
        if (topics == null) {
            return new ArrayList<>();
        }
        return topics.stream().map(this::toResponseTopic).collect(Collectors.toList());
    }

    private TopicDto toResponseTopic(Topic topic) {
        if (topic == null) {
            return null;
        }
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getName());
        dto.setDuration(topic.getDuration());
        dto.setIsFree(topic.getIsFree());
        dto.setVersion(topic.getVersion());
        return dto;
    }
}
