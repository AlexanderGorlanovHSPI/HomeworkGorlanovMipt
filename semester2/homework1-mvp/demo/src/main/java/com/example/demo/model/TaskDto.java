package com.example.demo.model;

import java.util.Objects;

/**
 * DTO для передачи данных задачи через REST API.
 *
 * <p>Используется в контроллере для разделения внешнего API-контракта и внутренней модели
 * хранения данных.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 */
public class TaskDto {
  private Long id;
  private String title;
  private String description;
  private boolean completed;

  public TaskDto() {}

  public TaskDto(Long id, String title, String description, boolean completed) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.completed = completed;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskDto taskDto = (TaskDto) o;
    return completed == taskDto.completed
        && Objects.equals(id, taskDto.id)
        && Objects.equals(title, taskDto.title)
        && Objects.equals(description, taskDto.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, completed);
  }

  @Override
  public String toString() {
    return "TaskDto{"
        + "id="
        + id
        + ", title='"
        + title
        + '\''
        + ", description='"
        + description
        + '\''
        + ", completed="
        + completed
        + '}';
  }
}
