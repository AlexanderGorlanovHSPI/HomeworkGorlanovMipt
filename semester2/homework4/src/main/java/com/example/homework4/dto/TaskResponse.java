package com.example.homework4.dto;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Boolean completed
) {
}
