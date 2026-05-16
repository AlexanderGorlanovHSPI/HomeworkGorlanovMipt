package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task_attachments")
public class TaskAttachment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "task_id", nullable = false)
  private Long taskId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", insertable = false, updatable = false)
  private Task task;

  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @Column(name = "stored_file_name", nullable = false, length = 255, unique = true)
  private String storedFileName;

  @Column(name = "content_type", length = 100)
  private String contentType;

  @Column(nullable = false)
  private long size;

  @Column(name = "uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;

  public TaskAttachment() {}

  public TaskAttachment(
      Long id,
      Long taskId,
      String fileName,
      String storedFileName,
      String contentType,
      long size,
      LocalDateTime uploadedAt) {
    this.id = id;
    this.taskId = taskId;
    this.fileName = fileName;
    this.storedFileName = storedFileName;
    this.contentType = contentType;
    this.size = size;
    this.uploadedAt = uploadedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getStoredFileName() {
    return storedFileName;
  }

  public void setStoredFileName(String storedFileName) {
    this.storedFileName = storedFileName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public LocalDateTime getUploadedAt() {
    return uploadedAt;
  }

  public void setUploadedAt(LocalDateTime uploadedAt) {
    this.uploadedAt = uploadedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskAttachment that = (TaskAttachment) o;
    return size == that.size
        && Objects.equals(id, that.id)
        && Objects.equals(taskId, that.taskId)
        && Objects.equals(fileName, that.fileName)
        && Objects.equals(storedFileName, that.storedFileName)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(uploadedAt, that.uploadedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, taskId, fileName, storedFileName, contentType, size, uploadedAt);
  }

  @Override
  public String toString() {
    return "TaskAttachment{"
        + "id="
        + id
        + ", taskId="
        + taskId
        + ", fileName='"
        + fileName
        + '\''
        + ", storedFileName='"
        + storedFileName
        + '\''
        + ", contentType='"
        + contentType
        + '\''
        + ", size="
        + size
        + ", uploadedAt="
        + uploadedAt
        + '}';
  }
}
