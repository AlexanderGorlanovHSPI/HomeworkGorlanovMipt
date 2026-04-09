package com.example.demo.repository;

import com.example.demo.model.TaskAttachment;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {
  private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public TaskAttachment save(TaskAttachment attachment) {
    if (attachment.getId() == null) {
      attachment.setId(idGenerator.getAndIncrement());
    }
    storage.put(attachment.getId(), attachment);
    return attachment;
  }

  @Override
  public Optional<TaskAttachment> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<TaskAttachment> findByTaskId(Long taskId) {
    return storage.values().stream().filter(a -> taskId.equals(a.getTaskId())).toList();
  }

  @Override
  public boolean existsById(Long id) {
    return storage.containsKey(id);
  }

  @Override
  public void deleteById(Long id) {
    storage.remove(id);
  }
}
