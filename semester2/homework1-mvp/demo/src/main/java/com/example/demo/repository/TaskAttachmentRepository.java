package com.example.demo.repository;

import com.example.demo.model.TaskAttachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
  List<TaskAttachment> findByTaskId(Long taskId);
}
