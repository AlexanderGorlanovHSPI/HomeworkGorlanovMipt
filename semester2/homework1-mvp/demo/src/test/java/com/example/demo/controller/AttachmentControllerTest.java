package com.example.demo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.TaskAttachment;
import com.example.demo.service.AttachmentService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttachmentService attachmentService;

  @Test
  void uploadAttachment_WithValidFile_ShouldReturnCreated() throws Exception {
    TaskAttachment attachment =
        new TaskAttachment(
            10L,
            1L,
            "file.txt",
            "stored-file.txt",
            "text/plain",
            4L,
            LocalDateTime.of(2026, 4, 6, 20, 0));
    when(attachmentService.storeAttachment(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
        .thenReturn(attachment);

    MockMultipartFile file =
        new MockMultipartFile("file", "file.txt", "text/plain", "test".getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(multipart("/api/tasks/1/attachments").file(file))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.fileName").value("file.txt"))
        .andExpect(jsonPath("$.size").value(4));
  }

  @Test
  void uploadAttachment_WithMissingTask_ShouldReturnNotFound() throws Exception {
    when(attachmentService.storeAttachment(org.mockito.ArgumentMatchers.eq(999L), org.mockito.ArgumentMatchers.any()))
        .thenThrow(new TaskNotFoundException(999L));

    MockMultipartFile file =
        new MockMultipartFile("file", "file.txt", "text/plain", "test".getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(multipart("/api/tasks/999/attachments").file(file))
        .andExpect(status().isNotFound());
  }

  @Test
  void downloadAttachment_WithExistingAttachment_ShouldReturnFile() throws Exception {
    TaskAttachment attachment =
        new TaskAttachment(
            10L,
            1L,
            "file.txt",
            "stored-file.txt",
            "text/plain",
            4L,
            LocalDateTime.of(2026, 4, 6, 20, 0));
    when(attachmentService.getAttachment(10L)).thenReturn(attachment);
    when(attachmentService.loadAsResource(10L))
        .thenReturn(new ByteArrayResource("test".getBytes(StandardCharsets.UTF_8)));

    mockMvc
        .perform(get("/api/attachments/10"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", containsString("attachment")))
        .andExpect(content().string("test"));
  }

  @Test
  void downloadAttachment_WithMissingAttachment_ShouldReturnNotFound() throws Exception {
    when(attachmentService.getAttachment(404L)).thenThrow(new NoSuchElementException("not found"));

    mockMvc
        .perform(get("/api/attachments/404"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteAttachment_WithExistingAttachment_ShouldReturnNoContent() throws Exception {
    mockMvc
        .perform(delete("/api/attachments/10"))
        .andExpect(status().isNoContent());
  }

  @Test
  void listTaskAttachments_ShouldReturnAttachmentList() throws Exception {
    TaskAttachment first =
        new TaskAttachment(
            10L,
            1L,
            "file-1.txt",
            "stored-1.txt",
            "text/plain",
            4L,
            LocalDateTime.of(2026, 4, 6, 20, 0));
    TaskAttachment second =
        new TaskAttachment(
            11L,
            1L,
            "file-2.txt",
            "stored-2.txt",
            "text/plain",
            5L,
            LocalDateTime.of(2026, 4, 6, 20, 1));
    when(attachmentService.getTaskAttachments(1L)).thenReturn(List.of(first, second));

    mockMvc
        .perform(get("/api/tasks/1/attachments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(10))
        .andExpect(jsonPath("$[1].id").value(11));
  }
}
