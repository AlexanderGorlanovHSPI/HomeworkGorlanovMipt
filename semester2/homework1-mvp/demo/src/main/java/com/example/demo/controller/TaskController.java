package com.example.demo.controller;

import com.example.demo.model.PrototypeScopedBean;
import com.example.demo.model.RequestScopedBean;
import com.example.demo.model.Task;
import com.example.demo.model.TaskDto;
import com.example.demo.service.AppInfoService;
import com.example.demo.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectFactory;
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
 * @see TaskDto
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final RequestScopedBean requestScopedBean;
  private final ObjectFactory<PrototypeScopedBean> prototypeBeanFactory;
  private final AppInfoService appInfoService;

  /**
   * Конструктор с внедрением зависимости сервиса задач.
   *
   * @param taskService сервис для работы с задачами
   * @param requestScopedBean request-scoped бин для демонстрации области видимости
   * @param prototypeBeanFactory фабрика prototype-scoped бинов
   * @param appInfoService сервис с информацией о приложении
   */
  @Autowired
  public TaskController(
      TaskService taskService,
      RequestScopedBean requestScopedBean,
      ObjectFactory<PrototypeScopedBean> prototypeBeanFactory,
      AppInfoService appInfoService) {
    this.taskService = taskService;
    this.requestScopedBean = requestScopedBean;
    this.prototypeBeanFactory = prototypeBeanFactory;
    this.appInfoService = appInfoService;
  }

  /**
   * Получить все задачи.
   *
   * @return список всех задач с HTTP статусом 200 OK
   */
  @GetMapping
  public ResponseEntity<List<TaskDto>> getAllTasks() {
    List<TaskDto> tasks = taskService.getAllTasks().stream().map(this::toDto).toList();
    return ResponseEntity.ok(tasks);
  }

  /**
   * Получить задачу по её идентификатору.
   *
   * @param id идентификатор задачи
   * @return задача с указанным ID или HTTP статус 404 Not Found
   */
  @GetMapping("/{id}")
  public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
    return taskService
        .getTaskById(id)
        .map(task -> ResponseEntity.ok(toDto(task)))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Создать новую задачу.
   *
   * @param taskDto данные новой задачи (ID может быть null)
   * @return созданная задача с автоматически сгенерированным ID и статусом 201 Created
   */
  @PostMapping
  public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
    Task createdTask = taskService.createTask(toModel(taskDto));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdTask));
  }

  /**
   * Обновить существующую задачу. ID в пути должен совпадать с ID в теле запроса.
   *
   * @param id идентификатор обновляемой задачи (из пути)
   * @param taskDto обновленные данные задачи
   * @return обновленная задача с HTTP статусом 200 OK или 404 Not Found
   */
  @PutMapping("/{id}")
  public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
    Task task = toModel(taskDto);
    task.setId(id);

    if (!taskService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    Task updatedTask = taskService.updateTask(task);
    return ResponseEntity.ok(toDto(updatedTask));
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

  /**
   * Демонстрирует работу request-scoped бина.
   *
   * @param request объект HttpServletRequest для получения информации о клиенте
   * @return строковое представление состояния request-scoped бина
   */
  @GetMapping("/demo/request-scope")
  public String demoRequestScope(HttpServletRequest request) {
    requestScopedBean.setClientInfo(request.getRemoteAddr());

    return String.format(
        "Request Scope Demo:%nRequest ID: %s%nStart Time: %s%nBean: %s",
        requestScopedBean.getRequestId(),
        requestScopedBean.getFormattedStartTime(),
        requestScopedBean);
  }

  /**
   * Демонстрирует работу prototype-scoped бинов.
   *
   * @return строковое представление, показывающее различия между двумя экземплярами бина
   */
  @GetMapping("/demo/prototype-scope")
  public String demoPrototypeScope() {
    PrototypeScopedBean bean1 = prototypeBeanFactory.getObject();
    PrototypeScopedBean bean2 = prototypeBeanFactory.getObject();

    Long taskId1 = bean1.generateTaskId();
    Long taskId2 = bean2.generateTaskId();

    return String.format(
        "Prototype Scope Demo:%nBean1 #%d ID: %s%nBean2 #%d ID: %s%nTask IDs: %d, %d%nSame bean? %s",
        bean1.getInstanceNumber(),
        bean1.getBeanId(),
        bean2.getInstanceNumber(),
        bean2.getBeanId(),
        taskId1,
        taskId2,
        bean1 == bean2);
  }

  /**
   * Получает информацию о приложении.
   *
   * @return строка с названием, версией, описанием и портом приложения
   */
  @GetMapping("/demo/info")
  public String getAppInfo() {
    return appInfoService.getAppInfo();
  }

  private TaskDto toDto(Task task) {
    return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
  }

  private Task toModel(TaskDto taskDto) {
    return new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.isCompleted());
  }
}
