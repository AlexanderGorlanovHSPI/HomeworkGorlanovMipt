package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для управления задачами, реализующий бизнес-логику приложения.
 *
 * <p>Предоставляет CRUD операции с задачами и поддерживает кэширование для оптимизации доступа к
 * данным.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see Task
 * @see TaskRepository
 */
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

  @Autowired
  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  /**
   * Инициализирует кэш задач после создания бина.
   *
   * <p>Загружает все задачи из репозитория в кэш для быстрого доступа. Вызывается автоматически
   * после внедрения зависимостей.
   */
  @PostConstruct
  public void initCache() {
    System.out.println("【@PostConstruct】Инициализация кэша TaskService");

    List<Task> allTasks = taskRepository.findAll();
    for (Task task : allTasks) {
      taskCache.put(task.getId(), task);
    }

    System.out.println("【@PostConstruct】Загружено " + taskCache.size() + " задач в кэш");
  }

  /**
   * Очищает ресурсы перед уничтожением бина.
   *
   * <p>Освобождает кэш и выводит информацию о его состоянии. Вызывается автоматически при
   * завершении работы приложения.
   */
  @PreDestroy
  public void destroyCache() {
    System.out.println("【@PreDestroy】Очистка ресурсов TaskService");
    System.out.println("【@PreDestroy】В кэше было " + taskCache.size() + " задач");

    taskCache.clear();

    System.out.println("【@PreDestroy】Кэш очищен");
  }

  public Task createTask(Task task) {
    Task savedTask = taskRepository.save(task);
    taskCache.put(savedTask.getId(), savedTask);
    return savedTask;
  }

  public Optional<Task> getTaskById(Long id) {
    Task cachedTask = taskCache.get(id);
    if (cachedTask != null) {
      return Optional.of(cachedTask);
    }

    Optional<Task> task = taskRepository.findById(id);
    task.ifPresent(t -> taskCache.put(t.getId(), t));
    return task;
  }

  public List<Task> getAllTasks() {
    List<Task> allTasks = taskRepository.findAll();
    taskCache.clear();
    allTasks.forEach(task -> taskCache.put(task.getId(), task));
    return allTasks;
  }

  public Task updateTask(Task task) {
    Task updatedTask = taskRepository.update(task);
    taskCache.put(updatedTask.getId(), updatedTask);
    return updatedTask;
  }

  public void deleteTask(Long id) {
    taskRepository.deleteById(id);
    taskCache.remove(id);
  }

  public boolean existsById(Long id) {
    return taskCache.containsKey(id) || taskRepository.existsById(id);
  }
}
