package com.growup.common.config;

import com.growup.common.domain.model.enums.CourseLevel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCourseLevelConverter implements Converter<String, CourseLevel> {

    @Override
    public CourseLevel convert(String source) {
        return CourseLevel.fromValue(source);
    }
}