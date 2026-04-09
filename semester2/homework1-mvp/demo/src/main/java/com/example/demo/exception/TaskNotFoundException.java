package com.example.demo.exception;

public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(Long taskId) {
    super("Task not found with id: " + taskId);
  }
}
