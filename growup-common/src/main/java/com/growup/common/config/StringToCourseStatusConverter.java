package com.growup.common.config;

import com.growup.common.domain.model.enums.CourseStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCourseStatusConverter implements Converter<String, CourseStatus> {

    @Override
    public CourseStatus convert(String source) {
        return CourseStatus.fromValue(source);
    }
}