package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.example.demo.config.JpaAuditConfig;
import org.springframework.context.annotation.Import;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class TaskRepositoryDataJpaTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskAttachmentRepository taskAttachmentRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  void findByCompletedAndPriority_ShouldReturnMatchingTasks() {
    Task openHigh = newTask("Open HIGH", false, Priority.HIGH, LocalDate.now().plusDays(2), Set.of("study"));
    Task openLow = newTask("Open LOW", false, Priority.LOW, LocalDate.now().plusDays(2), Set.of("home"));
    Task doneHigh = newTask("Done HIGH", true, Priority.HIGH, LocalDate.now().plusDays(2), Set.of("done"));

    taskRepository.saveAll(List.of(openHigh, openLow, doneHigh));

    List<Task> result = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Open HIGH");
  }

  @Test
  void findDueInRange_ShouldReturnTasksWithinRange() {
    Task near = newTask("Near", false, Priority.MEDIUM, LocalDate.now().plusDays(3), Set.of("soon"));
    Task far = newTask("Far", false, Priority.MEDIUM, LocalDate.now().plusDays(20), Set.of("later"));

    taskRepository.saveAll(List.of(near, far));

    List<Task> result =
        taskRepository.findDueInRange(LocalDate.now(), LocalDate.now().plusDays(7));

    assertThat(result).extracting(Task::getTitle).containsExactly("Near");
  }

  @Test
  void saveTaskWithAttachmentAndTags_ShouldPersistRelations() {
    Task task = newTask("Task with attachment", false, Priority.HIGH, LocalDate.now().plusDays(5), Set.of("jpa", "data"));
    Task savedTask = taskRepository.save(task);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTaskId(savedTask.getId());
    attachment.setFileName("report.txt");
    attachment.setStoredFileName("stored-report.txt");
    attachment.setContentType("text/plain");
    attachment.setSize(128L);
    attachment.setUploadedAt(LocalDateTime.now());

    taskAttachmentRepository.save(attachment);

    entityManager.flush();
    entityManager.clear();

    List<TaskAttachment> attachments = taskAttachmentRepository.findByTaskId(savedTask.getId());
    assertThat(attachments).hasSize(1);
    assertThat(attachments.get(0).getFileName()).isEqualTo("report.txt");

    Task loaded =
        taskRepository.findAllWithAttachments().stream()
            .filter(t -> t.getId().equals(savedTask.getId()))
            .findFirst()
            .orElseThrow();

    assertThat(loaded.getTags()).containsExactlyInAnyOrder("jpa", "data");
    assertThat(loaded.getAttachments()).hasSize(1);
  }

  private Task newTask(
      String title, boolean completed, Priority priority, LocalDate dueDate, Set<String> tags) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription("desc-" + title);
    task.setCompleted(completed);
    task.setPriority(priority);
    task.setDueDate(dueDate);
    task.setTags(tags);
    return task;
  }
}
