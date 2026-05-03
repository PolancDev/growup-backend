package com.growup.course.application.service;

import com.growup.common.infrastructure.exception.ResourceNotFoundException;
import com.growup.course.domain.model.Course;
import com.growup.course.domain.model.CourseModule;
import com.growup.course.domain.model.InstructorInfo;
import com.growup.course.domain.model.Topic;
import com.growup.course.domain.port.out.CoursePersistencePort;
import com.growup.course.domain.port.out.InstructorLookupPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CoursePersistencePort coursePersistencePort;

    @Mock
    private InstructorLookupPort instructorLookupPort;

    @InjectMocks
    private CourseService courseService;

    private UUID courseId;
    private UUID instructorId;
    private Course course;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        instructorId = UUID.randomUUID();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .description("Description")
                .category("DEV")
                .level("BASICO")
                .price(99.0)
                .duration(BigDecimal.TEN)
                .publicationStatus("BORRADOR")
                .instructorId(instructorId)
                .syllabus(List.of(
                        CourseModule.builder()
                                .id(UUID.randomUUID())
                                .title("Module 1")
                                .order(1)
                                .topics(List.of(
                                        Topic.builder()
                                                .id(UUID.randomUUID())
                                                .name("Topic 1")
                                                .duration("2 hours")
                                                .isFree(true)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    @Test
    void createCourse_shouldSaveAndReturnCourse() {
        when(instructorLookupPort.findInstructorInfoById(instructorId))
                .thenReturn(InstructorInfo.builder().id(instructorId).name("John").build());
        when(coursePersistencePort.save(any(Course.class))).thenReturn(course);

        Course result = courseService.createCourse(course, instructorId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(coursePersistencePort).save(any(Course.class));
    }

    @Test
    void createCourse_shouldThrowWhenInstructorNotFound() {
        when(instructorLookupPort.findInstructorInfoById(instructorId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> courseService.createCourse(course, instructorId));
    }

    @Test
    void getCourseById_shouldReturnCourse() {
        when(coursePersistencePort.findById(courseId)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(courseId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());
    }

    @Test
    void updateCourse_shouldUpdateAndReturnCourse() {
        Course update = Course.builder()
                .name("Updated Name")
                .description("Updated Desc")
                .category("DEV")
                .level("MEDIO")
                .price(199.0)
                .publicationStatus("PUBLICADO")
                .syllabus(List.of(
                        CourseModule.builder()
                                .title("New Module")
                                .order(1)
                                .topics(List.of(
                                        Topic.builder()
                                                .name("New Topic")
                                                .duration("1 hour")
                                                .isFree(false)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        when(coursePersistencePort.findById(courseId)).thenReturn(Optional.of(course));
        when(coursePersistencePort.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = courseService.updateCourse(courseId, update, instructorId);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals("MEDIO", result.getLevel());
        assertEquals(199.0, result.getPrice());
        assertEquals("PUBLICADO", result.getPublicationStatus());
        verify(coursePersistencePort).save(any(Course.class));
    }

    @Test
    void updateCourse_shouldThrowAccessDeniedWhenNotOwner() {
        UUID otherUser = UUID.randomUUID();
        when(coursePersistencePort.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class,
                () -> courseService.updateCourse(courseId, Course.builder().build(), otherUser));
    }

    @Test
    void deleteCourse_shouldDeleteWhenOwner() {
        when(coursePersistencePort.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(coursePersistencePort).delete(courseId);

        assertDoesNotThrow(() -> courseService.deleteCourse(courseId, instructorId));
        verify(coursePersistencePort).delete(courseId);
    }

    @Test
    void deleteCourse_shouldThrowAccessDeniedWhenNotOwner() {
        UUID otherUser = UUID.randomUUID();
        when(coursePersistencePort.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class, () -> courseService.deleteCourse(courseId, otherUser));
    }

    @Test
    void getCoursesByInstructor_shouldReturnList() {
        when(coursePersistencePort.findByInstructorId(instructorId)).thenReturn(List.of(course));

        List<Course> result = courseService.getCoursesByInstructor(instructorId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCoursesByInstructor_shouldReturnEmptyListWhenNoCourses() {
        when(coursePersistencePort.findByInstructorId(instructorId)).thenReturn(Collections.emptyList());

        List<Course> result = courseService.getCoursesByInstructor(instructorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
