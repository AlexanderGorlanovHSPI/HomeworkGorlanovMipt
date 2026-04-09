package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.service.TaskService;
import com.example.demo.validation.OnCreate;
import com.example.demo.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Операции управления задачами")
public class TaskController {

  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @Autowired
  public TaskController(TaskService taskService, TaskMapper taskMapper) {
    this.taskService = taskService;
    this.taskMapper = taskMapper;
  }

  @GetMapping
  @Operation(summary = "Получить список задач")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Список задач",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class))))
  })
  public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
    List<TaskResponseDto> tasks = taskMapper.toResponseDtoList(taskService.getAllTasks());
    return ResponseEntity.ok().header("X-Total-Count", String.valueOf(tasks.size())).body(tasks);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить задачу по идентификатору")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Задача найдена",
          content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
      @ApiResponse(
          responseCode = "404",
          description = "Задача не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
    Task task = taskService.getTaskByIdOrThrow(id);
    return ResponseEntity.ok(taskMapper.toResponseDto(task));
  }

  @PostMapping
  @Operation(summary = "Создать задачу")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Задача создана",
          content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Ошибка валидации",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<TaskResponseDto> createTask(
      @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
    Task createdTask = taskService.createTask(taskMapper.toEntity(dto));
    return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toResponseDto(createdTask));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить задачу")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Задача обновлена",
          content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Ошибка валидации",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
          responseCode = "404",
          description = "Задача не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<TaskResponseDto> updateTask(
      @PathVariable Long id, @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
    Task existing = taskService.getTaskByIdOrThrow(id);
    taskMapper.updateEntity(dto, existing);
    Task updatedTask = taskService.updateTask(existing);
    return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить задачу")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Задача удалена"),
      @ApiResponse(
          responseCode = "404",
          description = "Задача не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.getTaskByIdOrThrow(id);
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
