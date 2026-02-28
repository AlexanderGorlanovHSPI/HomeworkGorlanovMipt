package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления задачами. Предоставляет полный набор CRUD операций для работы с
 * задачами.
 *
 * <p>Базовый путь для всех эндпоинтов: /api/tasks
 *
 * @author Gorlanov Alexander
 * @version 1.0
 * @see TaskService
 * @see Task
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  /**
   * Конструктор с внедрением зависимости сервиса задач.
   *
   * @param taskService сервис для работы с задачами
   */
  @Autowired
  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  /**
   * Получить все задачи.
   *
   * @return список всех задач с HTTP статусом 200 OK
   */
  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    List<Task> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(tasks);
  }

  /**
   * Получить задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return задача с указанным ID или HTTP статус 404 Not Found
   */
  @GetMapping("/{id}")
  public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    return taskService
        .getTaskById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Создать новую задачу.
   *
   * @param task данные новой задачи (ID может быть null)
   * @return созданная задача с автоматически сгенерированным ID и статусом 201 Created
   */
  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    Task createdTask = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

  /**
   * Обновить существующую задачу. ID в пути должен совпадать с ID в теле запроса.
   *
   * @param id идентификатор обновляемой задачи (из пути)
   * @param task обновленные данные задачи
   * @return обновленная задача с HTTP статусом 200 OK или 404 Not Found
   */
  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
    task.setId(id);

    if (!taskService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    Task updatedTask = taskService.updateTask(task);
    return ResponseEntity.ok(updatedTask);
  }

  /**
   * Удалить задачу по идентификатору.
   *
   * @param id идентификатор удаляемой задачи
   * @return пустой ответ с HTTP статусом 204 No Content или 404 Not Found
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    if (!taskService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
