package com.example.demo.dto;

import com.example.demo.model.Priority;
import com.example.demo.validation.OnUpdate;
import com.example.demo.validation.annotation.DueDateNotBeforeCreation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@DueDateNotBeforeCreation(groups = OnUpdate.class)
@Schema(description = "DTO для частичного обновления задачи")
public class TaskUpdateDto {
  @Schema(example = "Обновленное название задачи")
  @Size(min = 3, max = 100, groups = OnUpdate.class)
  private String title;

  @Schema(example = "Обновленное описание")
  @Size(max = 500, groups = OnUpdate.class)
  private String description;

  @Schema(example = "true", description = "Статус выполнения")
  private Boolean completed;

  @Schema(example = "2026-04-11", description = "Новая плановая дата выполнения")
  @FutureOrPresent(groups = OnUpdate.class)
  private LocalDate dueDate;

  @Schema(example = "MEDIUM")
  private Priority priority;

  @Schema(description = "Новый набор тегов", example = "[\"update\", \"important\"]")
  @Size(max = 5, groups = OnUpdate.class)
  private Set<String> tags;

  public TaskUpdateDto() {}

  public TaskUpdateDto(
      String title,
      String description,
      Boolean completed,
      LocalDate dueDate,
      Priority priority,
      Set<String> tags) {
    this.title = title;
    this.description = description;
    this.completed = completed;
    this.dueDate = dueDate;
    this.priority = priority;
    this.tags = tags;
  }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Boolean getCompleted() { return completed; }
  public void setCompleted(Boolean completed) { this.completed = completed; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }
}
