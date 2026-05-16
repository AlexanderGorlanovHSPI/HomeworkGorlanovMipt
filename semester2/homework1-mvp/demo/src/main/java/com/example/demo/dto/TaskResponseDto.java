package com.example.demo.dto;

import com.example.demo.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "DTO ответа с полными данными задачи")
public class TaskResponseDto {
  @Schema(example = "1")
  private Long id;
  @Schema(example = "Сделать домашку")
  private String title;
  @Schema(example = "Проверить, что все тесты проходят")
  private String description;
  @Schema(example = "false")
  private boolean completed;
  @Schema(example = "2026-04-06T20:00:00")
  private LocalDateTime createdAt;
  @Schema(example = "2026-04-10")
  private LocalDate dueDate;
  @Schema(example = "HIGH")
  private Priority priority;
  @Schema(example = "[\"study\", \"backend\"]")
  private Set<String> tags;

  public TaskResponseDto() {}

  public TaskResponseDto(
      Long id,
      String title,
      String description,
      boolean completed,
      LocalDateTime createdAt,
      LocalDate dueDate,
      Priority priority,
      Set<String> tags) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.completed = completed;
    this.createdAt = createdAt;
    this.dueDate = dueDate;
    this.priority = priority;
    this.tags = tags;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public boolean isCompleted() { return completed; }
  public void setCompleted(boolean completed) { this.completed = completed; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }
}
