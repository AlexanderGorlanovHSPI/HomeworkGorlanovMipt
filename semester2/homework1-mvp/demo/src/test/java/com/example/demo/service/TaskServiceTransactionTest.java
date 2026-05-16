package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.exception.BulkCompleteTaskException;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTransactionTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void bulkCompleteTasks_WhenMissingId_ShouldRollbackAll() {
    Task t1 = taskService.createTask(newTask("Task-1", Priority.HIGH));
    Task t2 = taskService.createTask(newTask("Task-2", Priority.MEDIUM));

    assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(t1.getId(), 999999L, t2.getId())))
        .isInstanceOf(BulkCompleteTaskException.class);

    Task reloaded1 = taskRepository.findById(t1.getId()).orElseThrow();
    Task reloaded2 = taskRepository.findById(t2.getId()).orElseThrow();

    assertThat(reloaded1.isCompleted()).isFalse();
    assertThat(reloaded2.isCompleted()).isFalse();
  }

  @Test
  void bulkCompleteTasks_WhenAllIdsExist_ShouldMarkAllCompleted() {
    Task t1 = taskService.createTask(newTask("Task-3", Priority.LOW));
    Task t2 = taskService.createTask(newTask("Task-4", Priority.HIGH));

    List<Task> updated = taskService.bulkCompleteTasks(List.of(t1.getId(), t2.getId()));

    assertThat(updated).hasSize(2);
    assertThat(updated).allMatch(Task::isCompleted);

    assertThat(taskRepository.findById(t1.getId()).orElseThrow().isCompleted()).isTrue();
    assertThat(taskRepository.findById(t2.getId()).orElseThrow().isCompleted()).isTrue();
  }

  private Task newTask(String title, Priority priority) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription("desc-" + title);
    task.setCompleted(false);
    task.setPriority(priority);
    task.setDueDate(LocalDate.now().plusDays(5));
    task.setTags(Set.of("tx"));
    return task;
  }
}
