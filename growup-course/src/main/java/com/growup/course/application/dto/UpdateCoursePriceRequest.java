package com.growup.course.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización parcial del precio de un curso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCoursePriceRequest {

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
}