package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // Добавляем тестовый профиль
public class TaskControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private TaskService taskService;

  private Task task1;
  private Task task2;
  private Task newTask;

  @BeforeEach
  void setUp() {
    task1 = new Task(1L, "Тестовая задача 1", "Описание 1", false);
    task2 = new Task(2L, "Тестовая задача 2", "Описание 2", true);
    newTask = new Task(null, "Новая задача", "Новое описание", false);
  }

  @Test
  void getAllTasks_ShouldReturnListOfTasks() {
    List<Task> tasks = Arrays.asList(task1, task2);
    when(taskService.getAllTasks()).thenReturn(tasks);

    ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    verify(taskService, times(1)).getAllTasks();
  }

  @Test
  void getTaskById_WithExistingId_ShouldReturnTask() {
    when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));

    ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/1", Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(1L);
    verify(taskService, times(1)).getTaskById(1L);
  }

  @Test
  void getTaskById_WithNonExistingId_ShouldReturnNotFound() {
    when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

    ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/999", Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(taskService, times(1)).getTaskById(999L);
  }

  @Test
  void createTask_WithValidData_ShouldReturnCreatedTask() {
    Task savedTask = new Task(3L, "Новая задача", "Новое описание", false);
    when(taskService.createTask(any(Task.class))).thenReturn(savedTask);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Task> request = new HttpEntity<>(newTask, headers);

    ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", request, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(3L);
    verify(taskService, times(1)).createTask(any(Task.class));
  }

  @Test
  void updateTask_WithExistingId_ShouldReturnUpdatedTask() {
    Task updatedTask = new Task(1L, "Обновленная задача", "Обновленное описание", true);
    when(taskService.existsById(1L)).thenReturn(true);
    when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask, headers);

    ResponseEntity<Task> response = restTemplate.exchange(
            "/api/tasks/1",
            HttpMethod.PUT,
            requestEntity,
            Task.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(1L);
    verify(taskService, times(1)).existsById(1L);
    verify(taskService, times(1)).updateTask(any(Task.class));
  }

  @Test
  void updateTask_WithNonExistingId_ShouldReturnNotFound() {
    Task updatedTask = new Task(999L, "Обновленная", "Описание", true);
    when(taskService.existsById(999L)).thenReturn(false);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask, headers);

    ResponseEntity<Task> response = restTemplate.exchange(
            "/api/tasks/999",
            HttpMethod.PUT,
            requestEntity,
            Task.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(taskService, times(1)).existsById(999L);
    verify(taskService, never()).updateTask(any(Task.class));
  }

  @Test
  void deleteTask_WithExistingId_ShouldReturnNoContent() {
    when(taskService.existsById(1L)).thenReturn(true);
    doNothing().when(taskService).deleteTask(1L);

    ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tasks/1",
            HttpMethod.DELETE,
            null,
            Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(taskService, times(1)).existsById(1L);
    verify(taskService, times(1)).deleteTask(1L);
  }

  @Test
  void deleteTask_WithNonExistingId_ShouldReturnNotFound() {
    when(taskService.existsById(999L)).thenReturn(false);

    ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tasks/999",
            HttpMethod.DELETE,
            null,
            Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(taskService, times(1)).existsById(999L);
    verify(taskService, never()).deleteTask(anyLong());
  }
}