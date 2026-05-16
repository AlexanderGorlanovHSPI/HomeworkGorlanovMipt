package com.example.demo.repository;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;


public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

  @Query("select t from Task t where t.dueDate between :from and :to")
  List<Task> findDueInRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

  default Task update(Task task) {
    return save(task);
  }

  @EntityGraph(attributePaths = "attachments")
  @Query("select distinct t from Task t")
  List<Task> findAllWithAttachments();
}
