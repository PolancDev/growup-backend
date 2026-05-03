package com.growup.course.infrastructure.adapter.persistence.mapper;

import com.growup.common.domain.model.enums.CourseLevel;
import com.growup.common.domain.model.enums.CourseStatus;
import com.growup.course.domain.model.Course;
import com.growup.course.domain.model.CourseModule;
import com.growup.course.domain.model.Topic;
import com.growup.course.infrastructure.adapter.persistence.jpa.entity.CourseJpaEntity;
import com.growup.course.infrastructure.adapter.persistence.jpa.entity.CourseModuleJpaEntity;
import com.growup.course.infrastructure.adapter.persistence.jpa.entity.TopicJpaEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre el modelo de dominio Course y la entidad JPA
 * CourseJpaEntity.
 */
@Component
public class CoursePersistenceMapper {

    public Course toDomain(CourseJpaEntity entity) {
        if (entity == null)
            return null;

        Course course = new Course();
        course.setId(entity.getId());
        course.setName(entity.getName());
        course.setDescription(entity.getDescription());
        course.setImageUrl(toUri(entity.getImageUrl()));
        course.setCategory(entity.getCategory());
        course.setLevel(mapLevel(entity.getLevel()));
        course.setPrice(entity.getPrice());
        course.setDuration(entity.getDuration());
        course.setStartDate(entity.getStartDate());
        course.setEndDate(entity.getEndDate());
        course.setPublicationStatus(mapStatus(entity.getPublicationStatus()));
        course.setCreatedAt(entity.getCreatedAt());
        course.setUpdatedAt(entity.getUpdatedAt());
        course.setInstructorId(entity.getInstructorId());
        course.setSyllabus(mapModuleListToDomain(entity.getModules()));
        course.setEnrolledCount(entity.getEnrolledCount());
        course.setVersion(entity.getVersion());
        return course;
    }

    public CourseJpaEntity toEntity(Course course) {
        if (course == null)
            return null;

        CourseJpaEntity entity = new CourseJpaEntity();
        entity.setId(course.getId());
        entity.setName(course.getName());
        entity.setDescription(course.getDescription());
        entity.setImageUrl(toString(course.getImageUrl()));
        entity.setCategory(course.getCategory());
        entity.setLevel(mapLevel(course.getLevel()));
        entity.setPrice(course.getPrice());
        entity.setDuration(course.getDuration());
        entity.setStartDate(course.getStartDate());
        entity.setEndDate(course.getEndDate());
        entity.setPublicationStatus(mapStatus(course.getPublicationStatus()));
        entity.setEnrolledCount(course.getEnrolledCount());
        entity.setInstructorId(course.getInstructorId());
        entity.setVersion(course.getVersion() != null ? course.getVersion() : 0L);

        List<CourseModuleJpaEntity> modules = mapModuleListToEntity(course.getSyllabus());
        if (modules != null) {
            modules.forEach(m -> m.setCourse(entity));
        }
        entity.setModules(modules != null ? modules : new ArrayList<>());

        return entity;
    }

    public void updateEntity(CourseJpaEntity existing, Course course) {
        if (existing == null || course == null) {
            return;
        }

        existing.setName(course.getName());
        existing.setDescription(course.getDescription());
        existing.setImageUrl(toString(course.getImageUrl()));
        existing.setCategory(course.getCategory());
        existing.setLevel(mapLevel(course.getLevel()));
        existing.setPrice(course.getPrice());
        existing.setDuration(course.getDuration());
        existing.setStartDate(course.getStartDate());
        existing.setEndDate(course.getEndDate());
        existing.setPublicationStatus(mapStatus(course.getPublicationStatus()));
        existing.setEnrolledCount(course.getEnrolledCount());
        existing.setInstructorId(course.getInstructorId());
        if (course.getVersion() != null) {
            existing.setVersion(course.getVersion());
        }

        // Merge modules
        List<CourseModuleJpaEntity> existingModules = existing.getModules();

        // Remove modules not present in the request
        if (course.getSyllabus() == null || course.getSyllabus().isEmpty()) {
            existingModules.clear();
        } else {
            existingModules.removeIf(m -> course.getSyllabus().stream()
                    .noneMatch(nm -> nm.getId() != null && nm.getId().equals(m.getId())));

            for (CourseModule module : course.getSyllabus()) {
                CourseModuleJpaEntity moduleEntity = existingModules.stream()
                        .filter(m -> module.getId() != null && module.getId().equals(m.getId()))
                        .findFirst()
                        .orElse(null);

                if (moduleEntity == null) {
                    moduleEntity = toEntityModule(module);
                    moduleEntity.setCourse(existing);
                    existingModules.add(moduleEntity);
                } else {
                    moduleEntity.setTitle(module.getTitle());
                    moduleEntity.setDescription(module.getDescription());
                    moduleEntity.setOrder(module.getOrder());
                    if (module.getVersion() != null) {
                        moduleEntity.setVersion(module.getVersion());
                    }

                    // Merge topics
                    List<TopicJpaEntity> existingTopics = moduleEntity.getTopics();
                    if (module.getTopics() == null || module.getTopics().isEmpty()) {
                        existingTopics.clear();
                    } else {
                        existingTopics.removeIf(t -> module.getTopics().stream()
                                .noneMatch(nt -> nt.getId() != null && nt.getId().equals(t.getId())));

                        for (Topic topic : module.getTopics()) {
                            TopicJpaEntity topicEntity = existingTopics.stream()
                                    .filter(t -> topic.getId() != null && topic.getId().equals(t.getId()))
                                    .findFirst()
                                    .orElse(null);

                            if (topicEntity == null) {
                                topicEntity = toEntityTopic(topic);
                                topicEntity.setModule(moduleEntity);
                                existingTopics.add(topicEntity);
                            } else {
                                topicEntity.setTitle(topic.getName());
                                topicEntity.setDuration(mapDurationToInt(topic.getDuration()));
                                topicEntity.setIsFree(topic.getIsFree());
                                if (topic.getVersion() != null) {
                                    topicEntity.setVersion(topic.getVersion());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public CourseModule toDomainModule(CourseModuleJpaEntity entity) {
        if (entity == null)
            return null;

        CourseModule module = new CourseModule();
        module.setId(entity.getId());
        module.setCourseId(entity.getCourse() != null ? entity.getCourse().getId() : null);
        module.setTitle(entity.getTitle());
        module.setDescription(entity.getDescription());
        module.setOrder(entity.getOrder());
        module.setTopics(mapTopicListToDomain(entity.getTopics()));
        module.setVersion(entity.getVersion());
        return module;
    }

    public CourseModuleJpaEntity toEntityModule(CourseModule module) {
        if (module == null)
            return null;

        CourseModuleJpaEntity entity = new CourseModuleJpaEntity();
        entity.setId(module.getId());
        entity.setTitle(module.getTitle());
        entity.setDescription(module.getDescription());
        entity.setOrder(module.getOrder());
        List<TopicJpaEntity> topics = mapTopicListToEntity(module.getTopics());
        if (topics != null) {
            topics.forEach(topic -> topic.setModule(entity));
        }
        entity.setTopics(topics);
        entity.setVersion(module.getVersion() != null ? module.getVersion() : 0L);
        return entity;
    }

    public Topic toDomainTopic(TopicJpaEntity entity) {
        if (entity == null)
            return null;

        Topic topic = new Topic();
        topic.setId(entity.getId());
        topic.setName(entity.getTitle());
        topic.setDuration(mapDurationToString(entity.getDuration()));
        topic.setIsFree(entity.getIsFree());
        topic.setVersion(entity.getVersion());
        return topic;
    }

    public TopicJpaEntity toEntityTopic(Topic topic) {
        if (topic == null)
            return null;

        TopicJpaEntity entity = new TopicJpaEntity();
        entity.setId(topic.getId());
        entity.setTitle(topic.getName());
        entity.setDuration(mapDurationToInt(topic.getDuration()));
        entity.setIsFree(topic.getIsFree());
        entity.setVersion(topic.getVersion() != null ? topic.getVersion() : 0L);
        return entity;
    }

    private List<CourseModule> mapModuleListToDomain(List<CourseModuleJpaEntity> entities) {
        if (entities == null)
            return new ArrayList<>();
        return entities.stream().map(this::toDomainModule).collect(Collectors.toList());
    }

    private List<CourseModuleJpaEntity> mapModuleListToEntity(List<CourseModule> modules) {
        if (modules == null)
            return new ArrayList<>();
        return modules.stream().map(this::toEntityModule).collect(Collectors.toList());
    }

    private List<Topic> mapTopicListToDomain(List<TopicJpaEntity> entities) {
        if (entities == null)
            return new ArrayList<>();
        return entities.stream().map(this::toDomainTopic).collect(Collectors.toList());
    }

    private List<TopicJpaEntity> mapTopicListToEntity(List<Topic> topics) {
        if (topics == null)
            return new ArrayList<>();
        return topics.stream().map(this::toEntityTopic).collect(Collectors.toList());
    }

    private URI toUri(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return URI.create(value);
        } catch (Exception e) {
            return null;
        }
    }

    private String toString(URI value) {
        return value != null ? value.toString() : null;
    }

    private String mapLevel(CourseLevel level) {
        return level != null ? level.getValue() : null;
    }

    private CourseLevel mapLevel(String level) {
        if (level == null)
            return null;
        try {
            return CourseLevel.fromValue(level);
        } catch (Exception e) {
            return null;
        }
    }

    private String mapStatus(CourseStatus status) {
        return status != null ? status.getValue() : null;
    }

    private CourseStatus mapStatus(String status) {
        if (status == null)
            return null;
        try {
            return CourseStatus.fromValue(status);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer mapDurationToInt(String duration) {
        if (duration == null)
            return null;
        try {
            return Integer.parseInt(duration.replace(" hours", "").replace(" hour", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String mapDurationToString(Integer duration) {
        return duration != null ? duration + " hours" : null;
    }
}
