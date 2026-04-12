package com.example.demo.controller;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private TaskService taskService;

  private Task task1;
  private Task task2;
  private TaskCreateDto newTask;

  @BeforeEach
  void setUp() {
    task1 = new Task(1L, "Тестовая задача 1", "Описание 1", false);
    task2 = new Task(2L, "Тестовая задача 2", "Описание 2", true);
    newTask =
        new TaskCreateDto(
            "Новая задача",
            "Новое описание",
            LocalDate.now().plusDays(1),
            Priority.MEDIUM,
            Set.of("homework"));
  }

  @Test
  void getAllTasks_ShouldReturnListOfTasks() {
    List<Task> tasks = Arrays.asList(task1, task2);
    when(taskService.getAllTasks()).thenReturn(tasks);

    ResponseEntity<TaskResponseDto[]> response =
        restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");
    assertThat(response.getHeaders().getFirst("X-API-Version")).isEqualTo("2.0.0");
    verify(taskService, times(1)).getAllTasks();
  }

  @Test
  void getTaskById_WithExistingId_ShouldReturnTask() {
    when(taskService.getTaskByIdOrThrow(1L)).thenReturn(task1);

    ResponseEntity<TaskResponseDto> response =
        restTemplate.getForEntity("/api/tasks/1", TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(1L);
    verify(taskService, times(1)).getTaskByIdOrThrow(1L);
  }

  @Test
  void getTaskById_WithNonExistingId_ShouldReturnNotFound() {
    when(taskService.getTaskByIdOrThrow(999L)).thenThrow(new TaskNotFoundException(999L));

    ResponseEntity<TaskResponseDto> response =
        restTemplate.getForEntity("/api/tasks/999", TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(taskService, times(1)).getTaskByIdOrThrow(999L);
  }

  @Test
  void createTask_WithValidData_ShouldReturnCreatedTask() {
    Task savedTask = new Task(3L, "Новая задача", "Новое описание", false);
    when(taskService.createTask(any(Task.class))).thenReturn(savedTask);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<TaskCreateDto> request = new HttpEntity<>(newTask, headers);

    ResponseEntity<TaskResponseDto> response =
        restTemplate.postForEntity("/api/tasks", request, TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(3L);
    verify(taskService, times(1)).createTask(any(Task.class));
  }

  @Test
  void updateTask_WithExistingId_ShouldReturnUpdatedTask() {
    Task updatedTask = new Task(1L, "Обновленная задача", "Обновленное описание", true);
    TaskUpdateDto updatedTaskDto =
        new TaskUpdateDto(
            "Обновленная задача",
            "Обновленное описание",
            true,
            LocalDate.now().plusDays(2),
            Priority.HIGH,
            Set.of("updated"));
    when(taskService.getTaskByIdOrThrow(1L)).thenReturn(task1);
    when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<TaskUpdateDto> requestEntity = new HttpEntity<>(updatedTaskDto, headers);

    ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
            "/api/tasks/1",
            HttpMethod.PUT,
            requestEntity,
            TaskResponseDto.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(1L);
    verify(taskService, times(1)).getTaskByIdOrThrow(1L);
    verify(taskService, times(1)).updateTask(any(Task.class));
  }

  @Test
  void updateTask_WithNonExistingId_ShouldReturnNotFound() {
    TaskUpdateDto updatedTask =
        new TaskUpdateDto(
            "Обновленная",
            "Описание",
            true,
            LocalDate.now().plusDays(2),
            Priority.HIGH,
            Set.of("updated"));
    when(taskService.getTaskByIdOrThrow(999L)).thenThrow(new TaskNotFoundException(999L));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<TaskUpdateDto> requestEntity = new HttpEntity<>(updatedTask, headers);

    ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
            "/api/tasks/999",
            HttpMethod.PUT,
            requestEntity,
            TaskResponseDto.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(taskService, times(1)).getTaskByIdOrThrow(999L);
    verify(taskService, never()).updateTask(any(Task.class));
  }

  @Test
  void deleteTask_WithExistingId_ShouldReturnNoContent() {
    when(taskService.getTaskByIdOrThrow(1L)).thenReturn(task1);
    doNothing().when(taskService).deleteTask(1L);

    ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tasks/1",
            HttpMethod.DELETE,
            null,
            Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(taskService, times(1)).getTaskByIdOrThrow(1L);
    verify(taskService, times(1)).deleteTask(1L);
  }

  @Test
  void deleteTask_WithNonExistingId_ShouldReturnNotFound() {
    when(taskService.getTaskByIdOrThrow(999L)).thenThrow(new TaskNotFoundException(999L));

    ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tasks/999",
            HttpMethod.DELETE,
            null,
            Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(taskService, times(1)).getTaskByIdOrThrow(999L);
    verify(taskService, never()).deleteTask(anyLong());
  }
}
