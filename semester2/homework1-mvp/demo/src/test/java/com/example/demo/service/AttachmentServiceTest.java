package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

class AttachmentServiceTest {

  @TempDir
  Path tempDir;

  private TaskService taskService;
  private AttachmentService attachmentService;

  @BeforeEach
  void setUp() {
    taskService = Mockito.mock(TaskService.class);
    TaskAttachmentRepository attachmentRepository = Mockito.mock(TaskAttachmentRepository.class);

    Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    AtomicLong idGenerator = new AtomicLong(1);

    when(attachmentRepository.save(any(TaskAttachment.class)))
        .thenAnswer(
            invocation -> {
              TaskAttachment attachment = invocation.getArgument(0);
              if (attachment.getId() == null) {
                attachment.setId(idGenerator.getAndIncrement());
              }
              storage.put(attachment.getId(), attachment);
              return attachment;
            });

    when(attachmentRepository.findById(anyLong()))
        .thenAnswer(invocation -> Optional.ofNullable(storage.get(invocation.getArgument(0))));

    when(attachmentRepository.findByTaskId(anyLong()))
        .thenAnswer(
            invocation -> {
              Long taskId = invocation.getArgument(0);
              return storage.values().stream().filter(a -> taskId.equals(a.getTaskId())).toList();
            });

    doAnswer(
            invocation -> {
              Long id = invocation.getArgument(0);
              storage.remove(id);
              return null;
            })
        .when(attachmentRepository)
        .deleteById(anyLong());

    attachmentService = new AttachmentService(attachmentRepository, taskService, tempDir.toString());
  }

  @Test
  void storeAttachment_ShouldSaveFileAndMetadata() {
    when(taskService.existsById(1L)).thenReturn(true);
    MockMultipartFile file =
        new MockMultipartFile("file", "doc.txt", "text/plain", "data".getBytes());

    TaskAttachment saved = attachmentService.storeAttachment(1L, file);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getTaskId()).isEqualTo(1L);
    assertThat(saved.getFileName()).isEqualTo("doc.txt");
    assertThat(saved.getStoredFileName()).isNotBlank();
    assertThat(Files.exists(tempDir.resolve(saved.getStoredFileName()))).isTrue();
  }

  @Test
  void storeAttachment_WhenTaskMissing_ShouldThrowTaskNotFoundException() {
    when(taskService.existsById(5L)).thenReturn(false);
    MockMultipartFile file =
        new MockMultipartFile("file", "doc.txt", "text/plain", "data".getBytes());

    assertThatThrownBy(() -> attachmentService.storeAttachment(5L, file))
        .isInstanceOf(TaskNotFoundException.class);
  }

  @Test
  void loadAsResource_ShouldReturnReadableResource() throws Exception {
    when(taskService.existsById(1L)).thenReturn(true);
    MockMultipartFile file =
        new MockMultipartFile("file", "doc.txt", "text/plain", "data".getBytes());
    TaskAttachment saved = attachmentService.storeAttachment(1L, file);

    var resource = attachmentService.loadAsResource(saved.getId());

    assertThat(resource.exists()).isTrue();
    assertThat(resource.isReadable()).isTrue();
    assertThat(resource.getContentAsString(java.nio.charset.StandardCharsets.UTF_8)).isEqualTo("data");
  }

  @Test
  void deleteAttachment_ShouldRemoveFileAndMetadata() {
    when(taskService.existsById(1L)).thenReturn(true);
    MockMultipartFile file =
        new MockMultipartFile("file", "doc.txt", "text/plain", "data".getBytes());
    TaskAttachment saved = attachmentService.storeAttachment(1L, file);

    Path path = tempDir.resolve(saved.getStoredFileName());
    assertThat(Files.exists(path)).isTrue();

    attachmentService.deleteAttachment(saved.getId());

    assertThat(Files.exists(path)).isFalse();
    assertThatThrownBy(() -> attachmentService.getAttachment(saved.getId()))
        .isInstanceOf(java.util.NoSuchElementException.class);
  }
}
