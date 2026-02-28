package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация репозитория задач, хранящая данные в оперативной памяти.
 *
 * <p>Использует {@link ConcurrentHashMap} для потокобезопасного хранения задач и {@link AtomicLong}
 * для генерации уникальных идентификаторов.
 *
 * <p>Является основным (primary) репозиторием приложения.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see TaskRepository
 * @see StubTaskRepository
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {
  private final Map<Long, Task> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

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
