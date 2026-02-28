package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления задачами. Содержит бизнес-логику приложения.
 *
 * <p>Использует {@link TaskRepository} для доступа к данным и поддерживает кэширование задач через
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see Task
 * @see TaskRepository
 */
@Service
public class TaskStatisticsService {

  private final TaskRepository primaryRepository;
  private final TaskRepository stubRepository;

  /**
   * Конструктор с внедрением двух различных реализаций репозитория.
   *
   * @param primaryRepository основной репозиторий (InMemoryTaskRepository)
   * @param stubRepository репозиторий-заглушка (StubTaskRepository), идентифицируемый по имени бина
   *     "stubTaskRepository"
   */
  @Autowired
  public TaskStatisticsService(
      TaskRepository primaryRepository,
      @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
    this.primaryRepository = primaryRepository;
    this.stubRepository = stubRepository;
  }

  /**
   * Демонстрирует и сравнивает содержимое двух репозиториев.
   *
   * <p>Выводит в консоль количество задач в каждом репозитории и список задач из
   * репозитория-заглушки.
   */
  public void compareRepositories() {
    System.out.println("=== Сравнение репозиториев ===");

    List<Task> primaryTasks = primaryRepository.findAll();
    System.out.println(
        "Основной репозиторий (InMemory) содержит " + primaryTasks.size() + " задач");

    List<Task> stubTasks = stubRepository.findAll();
    System.out.println("Stub репозиторий содержит " + stubTasks.size() + " задач");
    stubTasks.forEach(task -> System.out.println("  - " + task.getTitle()));

    System.out.println("=== Конец сравнения ===");
  }

  /**
   * Добавляет задачу только в основной репозиторий.
   *
   * @param task задача для добавления
   * @return созданная задача с установленным ID
   */
  public Task addToPrimary(Task task) {
    return primaryRepository.save(task);
  }

  /**
   * Добавляет задачу только в репозиторий-заглушку.
   *
   * @param task задача для добавления
   * @return созданная задача с установленным ID
   */
  public Task addToStub(Task task) {
    return stubRepository.save(task);
  }
}
