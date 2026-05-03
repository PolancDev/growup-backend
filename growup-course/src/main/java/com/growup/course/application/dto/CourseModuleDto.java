package com.growup.course.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO para módulos del syllabus en las respuestas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseModuleDto {
    private UUID id;
    private String title;
    private String description;
    private Integer order;
    private Long version;
    private List<TopicDto> topics;
}
