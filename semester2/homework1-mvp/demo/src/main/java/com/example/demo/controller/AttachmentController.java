package com.example.demo.controller;

import com.example.demo.dto.AttachmentResponseDto;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.TaskAttachment;
import com.example.demo.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "Операции работы с вложениями задач")
public class AttachmentController {
  private final AttachmentService attachmentService;

  @Autowired
  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping("/tasks/{taskId}/attachments")
  @Operation(summary = "Загрузить вложение к задаче")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Файл успешно загружен",
          content = @Content(schema = @Schema(implementation = AttachmentResponseDto.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Невалидный файл",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
          responseCode = "404",
          description = "Задача не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<AttachmentResponseDto> uploadAttachment(
      @PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
    TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(attachment));
  }

  @GetMapping("/attachments/{attachmentId}")
  @Operation(summary = "Скачать вложение")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Файл найден и отправлен"),
      @ApiResponse(
          responseCode = "404",
          description = "Вложение не найдено",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
    TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
    Resource resource = attachmentService.loadAsResource(attachmentId);

    MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
    if (attachment.getContentType() != null && !attachment.getContentType().isBlank()) {
      mediaType = MediaType.parseMediaType(attachment.getContentType());
    }

    ContentDisposition disposition =
        ContentDisposition.attachment()
            .filename(attachment.getFileName(), StandardCharsets.UTF_8)
            .build();

    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
        .contentLength(attachment.getSize())
        .body(resource);
  }

  @DeleteMapping("/attachments/{attachmentId}")
  @Operation(summary = "Удалить вложение")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Вложение удалено"),
      @ApiResponse(
          responseCode = "404",
          description = "Вложение не найдено",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
    attachmentService.deleteAttachment(attachmentId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/tasks/{taskId}/attachments")
  @Operation(summary = "Получить список вложений задачи")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Список вложений",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentResponseDto.class)))),
      @ApiResponse(
          responseCode = "404",
          description = "Задача не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<AttachmentResponseDto>> getTaskAttachments(@PathVariable Long taskId) {
    List<AttachmentResponseDto> attachments =
        attachmentService.getTaskAttachments(taskId).stream().map(this::toResponseDto).toList();
    return ResponseEntity.ok(attachments);
  }

  private AttachmentResponseDto toResponseDto(TaskAttachment attachment) {
    return new AttachmentResponseDto(
        attachment.getId(),
        attachment.getFileName(),
        attachment.getSize(),
        attachment.getUploadedAt());
  }
}
