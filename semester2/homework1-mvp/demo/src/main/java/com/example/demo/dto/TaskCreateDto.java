package com.example.demo.dto;

import com.example.demo.model.Priority;
import com.example.demo.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO для создания задачи")
public class TaskCreateDto {
  @Schema(example = "Закрыть домашнее задание", description = "Название задачи")
  @NotBlank(groups = OnCreate.class)
  @Size(min = 3, max = 100, groups = OnCreate.class)
  private String title;

  @Schema(example = "Доделать все части ДЗ до пятницы", description = "Описание задачи")
  @Size(max = 500, groups = OnCreate.class)
  private String description;

  @Schema(example = "2026-04-10", description = "Плановая дата выполнения")
  @FutureOrPresent(groups = OnCreate.class)
  private LocalDate dueDate;

  @Schema(example = "HIGH", description = "Приоритет задачи")
  @NotNull(groups = OnCreate.class)
  private Priority priority;

  @Schema(description = "Список тегов задачи", example = "[\"study\", \"java\"]")
  @Size(max = 5, groups = OnCreate.class)
  private Set<String> tags;

  public TaskCreateDto() {}

  public TaskCreateDto(String title, String description, LocalDate dueDate, Priority priority, Set<String> tags) {
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.priority = priority;
    this.tags = tags;
  }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }
}
