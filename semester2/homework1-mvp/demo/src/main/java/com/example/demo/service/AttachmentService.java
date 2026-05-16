package com.example.demo.service;

import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService {
  private final TaskAttachmentRepository attachmentRepository;
  private final TaskService taskService;
  private final Path uploadDir;

  @Autowired
  public AttachmentService(
      TaskAttachmentRepository attachmentRepository,
      TaskService taskService,
      @Value("${app.attachments.upload-dir:uploads}") String uploadDir) {
    this.attachmentRepository = attachmentRepository;
    this.taskService = taskService;
    this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    initUploadDir();
  }

  public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
    if (!taskService.existsById(taskId)) {
      throw new TaskNotFoundException(taskId);
    }
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("File must not be empty");
    }

    String originalName = StringUtils.cleanPath(file.getOriginalFilename());
    String storedName = buildStoredFileName(originalName);
    Path targetPath = uploadDir.resolve(storedName).normalize();

    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to store file: " + originalName, ex);
    }

    TaskAttachment attachment =
        new TaskAttachment(
            null,
            taskId,
            originalName,
            storedName,
            file.getContentType(),
            file.getSize(),
            LocalDateTime.now());

    return attachmentRepository.save(attachment);
  }

  public TaskAttachment getAttachment(Long attachmentId) {
    return attachmentRepository
        .findById(attachmentId)
        .orElseThrow(() -> new NoSuchElementException("Attachment not found with id: " + attachmentId));
  }

  public List<TaskAttachment> getTaskAttachments(Long taskId) {
    if (!taskService.existsById(taskId)) {
      throw new TaskNotFoundException(taskId);
    }
    return attachmentRepository.findByTaskId(taskId);
  }

  public Resource loadAsResource(Long attachmentId) {
    TaskAttachment attachment = getAttachment(attachmentId);
    Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();

    try {
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists() && resource.isReadable()) {
        return resource;
      }
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to read file for attachment: " + attachmentId, ex);
    }

    throw new NoSuchElementException("Attachment file not found with id: " + attachmentId);
  }

  public void deleteAttachment(Long attachmentId) {
    TaskAttachment attachment = getAttachment(attachmentId);
    Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();

    try {
      Files.deleteIfExists(filePath);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to delete file for attachment: " + attachmentId, ex);
    }

    attachmentRepository.deleteById(attachmentId);
  }

  private void initUploadDir() {
    try {
      Files.createDirectories(uploadDir);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to create upload directory: " + uploadDir, ex);
    }
  }

  private String buildStoredFileName(String originalName) {
    if (originalName == null || originalName.isBlank()) {
      return UUID.randomUUID().toString();
    }

    int extensionIndex = originalName.lastIndexOf('.');
    String extension = extensionIndex >= 0 ? originalName.substring(extensionIndex) : "";
    return UUID.randomUUID() + extension;
  }
}
