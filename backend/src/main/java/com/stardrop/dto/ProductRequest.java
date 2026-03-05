package com.stardrop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank String name,
    String description,
    @NotNull @Positive BigDecimal price,
    String imageUrl,
    @PositiveOrZero int stock,
    @NotNull Long categoryId
) {}
