package com.example.demo.service;

import com.example.demo.exception.BulkCompleteTaskException;
import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      isolation = Isolation.READ_COMMITTED)
  public Task createTask(Task task) {
    return taskRepository.save(task);
  }

  @Transactional(
      readOnly = true,
      propagation = Propagation.SUPPORTS,
      isolation = Isolation.READ_COMMITTED)
  public java.util.Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }

  @Transactional(
      readOnly = true,
      propagation = Propagation.SUPPORTS,
      isolation = Isolation.READ_COMMITTED)
  public Task getTaskByIdOrThrow(Long id) {
    return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
  }

  @Transactional(
      readOnly = true,
      propagation = Propagation.SUPPORTS,
      isolation = Isolation.READ_COMMITTED)
  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  @Transactional(
      readOnly = true,
      propagation = Propagation.SUPPORTS,
      isolation = Isolation.READ_COMMITTED)
  public List<Task> getAllTasksWithAttachments() {
    return taskRepository.findAllWithAttachments();
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      isolation = Isolation.READ_COMMITTED)
  public Task updateTask(Task task) {
    return taskRepository.save(task);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      isolation = Isolation.READ_COMMITTED)
  public void deleteTask(Long id) {
    taskRepository.deleteById(id);
  }

  @Transactional(
      readOnly = true,
      propagation = Propagation.SUPPORTS,
      isolation = Isolation.READ_COMMITTED)
  public boolean existsById(Long id) {
    return taskRepository.existsById(id);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      isolation = Isolation.READ_COMMITTED,
      rollbackFor = BulkCompleteTaskException.class)
  public List<Task> bulkCompleteTasks(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    List<Task> tasks = taskRepository.findAllById(ids);

    Set<Long> found = new LinkedHashSet<>();
    for (Task task : tasks) {
      found.add(task.getId());
    }

    List<Long> missing = ids.stream().filter(id -> !found.contains(id)).distinct().toList();
    if (!missing.isEmpty()) {
      throw new BulkCompleteTaskException(missing);
    }

    for (Task task : tasks) {
      task.setCompleted(true);
    }

    return taskRepository.saveAll(tasks);
  }
}
