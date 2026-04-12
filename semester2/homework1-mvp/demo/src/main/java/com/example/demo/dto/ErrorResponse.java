package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Унифицированный ответ об ошибке")
public class ErrorResponse {
  @Schema(example = "2026-04-06T20:30:00Z")
  private Instant timestamp;
  @Schema(example = "400")
  private int status;
  @Schema(example = "Bad Request")
  private String error;
  @Schema(example = "Validation failed")
  private String message;
  @Schema(example = "/api/tasks")
  private String path;
  @Schema(description = "Дополнительные детали ошибки")
  private Map<String, Object> details;

  public ErrorResponse() {}

  public ErrorResponse(
      Instant timestamp,
      int status,
      String error,
      String message,
      String path,
      Map<String, Object> details) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
    this.details = details;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Map<String, Object> getDetails() {
    return details;
  }

  public void setDetails(Map<String, Object> details) {
    this.details = details;
  }
}
