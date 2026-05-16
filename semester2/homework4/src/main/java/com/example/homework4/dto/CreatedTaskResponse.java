package com.example.homework4.dto;

public record CreatedTaskResponse(
        TaskResponse task,
        String location
) {
}
