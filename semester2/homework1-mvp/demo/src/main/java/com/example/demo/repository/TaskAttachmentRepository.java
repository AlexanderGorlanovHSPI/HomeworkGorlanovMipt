package com.example.demo.repository;

import com.example.demo.model.TaskAttachment;
import java.util.List;
import java.util.Optional;

public interface TaskAttachmentRepository {
  TaskAttachment save(TaskAttachment attachment);

  Optional<TaskAttachment> findById(Long id);

  List<TaskAttachment> findByTaskId(Long taskId);

  boolean existsById(Long id);

  void deleteById(Long id);
}
