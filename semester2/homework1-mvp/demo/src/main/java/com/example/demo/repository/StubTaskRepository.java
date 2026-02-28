package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация репозитория задач-заглушек с предварительно заполненными тестовыми данными.
 *
 * <p>Хранит задачи в оперативной памяти с использованием {@link ConcurrentHashMap} и автоматически
 * инициализируется несколькими тестовыми задачами при создании.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see TaskRepository
 * @see InMemoryTaskRepository
 */
public class StubTaskRepository implements TaskRepository {

  private final Map<Long, Task> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  /**
   * Конструктор, инициализирующий репозиторий тестовыми данными.
   *
   * <p>При создании автоматически добавляет несколько задач для демонстрационных целей.
   */
  public StubTaskRepository() {
    save(new Task(null, "Купить продукты", "Молоко, хлеб, яйца", false));
    save(new Task(null, "Сделать домашку", "Выполнить задание по Spring", false));
    save(new Task(null, "Позвонить маме", "Узнать как дела", true));
    save(new Task(null, "Почитать книгу", "Закончить главу 5", false));
  }

  /**
   * Сохраняет задачу в репозитории.
   *
   * <p>Если у задачи отсутствует ID (null), генерирует новый уникальный идентификатор.
   *
   * @param task задача для сохранения
   * @return сохраненная задача с установленным ID
   */
  @Override
  public Task save(Task task) {
    if (task.getId() == null) {
      task.setId(idGenerator.getAndIncrement());
    }
    storage.put(task.getId(), task);
    return task;
  }

  @Override
  public Optional<Task> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public Task update(Task task) {
    if (task.getId() == null || !storage.containsKey(task.getId())) {
      throw new IllegalArgumentException("Task not found with id: " + task.getId());
    }
    storage.put(task.getId(), task);
    return task;
  }

  @Override
  public void deleteById(Long id) {
    storage.remove(id);
  }

  @Override
  public void deleteAll() {
    storage.clear();
  }

  @Override
  public boolean existsById(Long id) {
    return storage.containsKey(id);
  }
}
