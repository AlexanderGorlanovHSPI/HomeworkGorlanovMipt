package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO с метаданными вложения")
public class AttachmentResponseDto {
  @Schema(example = "101")
  private Long id;
  @Schema(example = "notes.pdf")
  private String fileName;
  @Schema(example = "102400")
  private long size;
  @Schema(example = "2026-04-06T20:05:00")
  private LocalDateTime uploadedAt;

  public AttachmentResponseDto() {}

  public AttachmentResponseDto(Long id, String fileName, long size, LocalDateTime uploadedAt) {
    this.id = id;
    this.fileName = fileName;
    this.size = size;
    this.uploadedAt = uploadedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
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
}
