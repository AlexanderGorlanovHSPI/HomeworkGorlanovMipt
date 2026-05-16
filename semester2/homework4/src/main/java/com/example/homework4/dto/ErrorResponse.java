package com.example.homework4.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        String code,
        String message,
        OffsetDateTime timestamp,
        String traceId
) {
}
