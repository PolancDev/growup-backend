package com.growup.course.infrastructure.adapter.web;

import com.growup.course.application.dto.CourseRequest;
import com.growup.course.application.dto.CourseResponse;
import com.growup.course.application.mapper.CourseDtoMapper;
import com.growup.course.application.service.CourseService;
import com.growup.course.config.SecurityConfig;
import com.growup.course.domain.model.Course;
import com.growup.common.domain.model.enums.CourseLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseWebAdapter.class)
@Import(SecurityConfig.class)
class CourseWebAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CourseDtoMapper courseDtoMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID courseId;
    private JwtAuthenticationToken jwtAuth;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        courseId = UUID.randomUUID();

        org.springframework.security.oauth2.jwt.Jwt jwt = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", userId.toString())
                .claim("realm_access", java.util.Map.of("roles", List.of("TEACHER")))
                .build();

        jwtAuth = new JwtAuthenticationToken(jwt, List.of(() -> "ROLE_TEACHER"));
        jwtAuth.setAuthenticated(true);
    }

    @Test
    void getCourses_shouldReturn200_withoutAuth() throws Exception {
        when(courseService.getAllCourses(null, null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void getCourseById_shouldReturn200_withoutAuth() throws Exception {
        Course course = Course.builder().id(courseId).name("Test").build();
        CourseResponse response = new CourseResponse();
        response.setId(courseId);
        response.setName("Test");

        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(courseDtoMapper.toResponse(course)).thenReturn(response);

        mockMvc.perform(get("/api/v1/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId.toString()));
    }

    @Test
    void createCourse_shouldReturn201_withJwt() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .name("New Course")
                .category("DEV")
                .level(CourseLevel.PRINCIPIANTE)
                .price(100.0)
                .build();

        Course domainCourse = Course.builder().id(courseId).name("New Course").build();
        CourseResponse response = new CourseResponse();
        response.setId(courseId);
        response.setName("New Course");

        when(courseDtoMapper.toDomain(any(CourseRequest.class))).thenReturn(domainCourse);
        when(courseService.createCourse(any(Course.class), eq(userId))).thenReturn(domainCourse);
        when(courseDtoMapper.toResponse(domainCourse)).thenReturn(response);

        mockMvc.perform(post("/api/v1/courses")
                        .with(authentication(jwtAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(courseId.toString()));
    }

    @Test
    void updateCourse_shouldReturn200_withOwner() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .name("Updated Course")
                .category("DEV")
                .level(CourseLevel.INTERMEDIO)
                .price(150.0)
                .build();

        Course domainCourse = Course.builder().id(courseId).name("Updated Course").build();
        CourseResponse response = new CourseResponse();
        response.setId(courseId);
        response.setName("Updated Course");

        when(courseDtoMapper.toDomain(any(CourseRequest.class))).thenReturn(domainCourse);
        when(courseService.updateCourse(eq(courseId), any(Course.class), eq(userId))).thenReturn(domainCourse);
        when(courseDtoMapper.toResponse(domainCourse)).thenReturn(response);

        mockMvc.perform(put("/api/v1/courses/{id}", courseId)
                        .with(authentication(jwtAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Course"));
    }

    @Test
    void deleteCourse_shouldReturn204_withOwner() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/{id}", courseId)
                        .with(authentication(jwtAuth)))
                .andExpect(status().isNoContent());
    }

    @Test
    void createCourse_shouldReturn401_withoutAuth() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .name("New Course")
                .category("DEV")
                .level(CourseLevel.PRINCIPIANTE)
                .price(100.0)
                .build();

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
