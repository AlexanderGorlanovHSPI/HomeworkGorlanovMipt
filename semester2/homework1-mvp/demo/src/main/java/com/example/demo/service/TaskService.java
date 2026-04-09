package com.example.demo.service;

import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

  @Autowired
  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
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

  public Task getTaskByIdOrThrow(Long id) {
    return getTaskById(id).orElseThrow(() -> new TaskNotFoundException(id));
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
