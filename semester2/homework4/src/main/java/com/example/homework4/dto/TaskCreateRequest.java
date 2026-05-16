package com.example.homework4.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequest(
        @NotBlank String title,
        String description,
        Boolean completed
) {
}
