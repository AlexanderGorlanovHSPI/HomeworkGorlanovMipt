package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TaskMapperTest {

  private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

  @Test
  void toEntity_ShouldMapCreateDto() {
    TaskCreateDto dto =
        new TaskCreateDto(
            "Task title",
            "Task description",
            LocalDate.of(2026, 4, 10),
            Priority.HIGH,
            Set.of("java", "homework"));

    Task entity = mapper.toEntity(dto);

    assertThat(entity.getId()).isNull();
    assertThat(entity.getTitle()).isEqualTo("Task title");
    assertThat(entity.getDescription()).isEqualTo("Task description");
    assertThat(entity.isCompleted()).isFalse();
    assertThat(entity.getCreatedAt()).isNotNull();
    assertThat(entity.getDueDate()).isEqualTo(LocalDate.of(2026, 4, 10));
    assertThat(entity.getPriority()).isEqualTo(Priority.HIGH);
    assertThat(entity.getTags()).containsExactlyInAnyOrder("java", "homework");
  }

  @Test
  void updateEntity_ShouldIgnoreNullFields() {
    Task existing =
        new Task(
            1L,
            "Initial title",
            "Initial description",
            false,
            LocalDateTime.of(2026, 4, 1, 10, 0),
            LocalDate.of(2026, 4, 12),
            Priority.MEDIUM,
            Set.of("legacy"));

    TaskUpdateDto updateDto = new TaskUpdateDto(null, "Updated description", true, null, null, null);
    mapper.updateEntity(updateDto, existing);

    assertThat(existing.getId()).isEqualTo(1L);
    assertThat(existing.getTitle()).isEqualTo("Initial title");
    assertThat(existing.getDescription()).isEqualTo("Updated description");
    assertThat(existing.isCompleted()).isTrue();
    assertThat(existing.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 1, 10, 0));
    assertThat(existing.getDueDate()).isEqualTo(LocalDate.of(2026, 4, 12));
    assertThat(existing.getPriority()).isEqualTo(Priority.MEDIUM);
    assertThat(existing.getTags()).containsExactly("legacy");
  }

  @Test
  void toResponseDto_ShouldMapEntity() {
    Task entity =
        new Task(
            7L,
            "Task title",
            "Task description",
            true,
            LocalDateTime.of(2026, 4, 6, 21, 0),
            LocalDate.of(2026, 4, 15),
            Priority.LOW,
            Set.of("study"));

    TaskResponseDto response = mapper.toResponseDto(entity);

    assertThat(response.getId()).isEqualTo(7L);
    assertThat(response.getTitle()).isEqualTo("Task title");
    assertThat(response.getDescription()).isEqualTo("Task description");
    assertThat(response.isCompleted()).isTrue();
    assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 6, 21, 0));
    assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2026, 4, 15));
    assertThat(response.getPriority()).isEqualTo(Priority.LOW);
    assertThat(response.getTags()).containsExactly("study");
  }
}
