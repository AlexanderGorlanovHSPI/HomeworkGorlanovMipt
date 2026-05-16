package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @Test
  void createTask_WithInvalidBody_ShouldReturnUnifiedValidationError() throws Exception {
    mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().string("X-API-Version", "2.0.0"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.path").value("/api/tasks"))
        .andExpect(jsonPath("$.details.fieldErrors.title").exists())
        .andExpect(jsonPath("$.details.fieldErrors.priority").exists());
  }

  @Test
  void setViewPreference_WithoutMode_ShouldReturnMissingParameterError() throws Exception {
    mockMvc.perform(post("/api/preferences/view").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().string("X-API-Version", "2.0.0"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.details.parameter").value("mode"))
        .andExpect(header().exists(HttpHeaders.CONTENT_TYPE));
  }
}
