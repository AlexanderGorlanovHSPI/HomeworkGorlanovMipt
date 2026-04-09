package com.example.demo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.service.TaskService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsAndOpenApiTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @Test
  void preflightRequest_ShouldReturnCorsHeaders() throws Exception {
    mockMvc
        .perform(
            options("/api/tasks")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
        .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
  }

  @Test
  void simpleCorsRequest_ShouldExposeCustomHeaders() throws Exception {
    when(taskService.getAllTasks()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/tasks").header("Origin", "http://localhost:3000"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
        .andExpect(header().string("Access-Control-Expose-Headers", containsString("X-Total-Count")))
        .andExpect(header().string("Access-Control-Expose-Headers", containsString("X-API-Version")));
  }

  @Test
  void apiDocs_ShouldBeAvailableWithConfiguredMetadata() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.info.title").value("To-Do List API"))
        .andExpect(jsonPath("$.info.version").value("2.0.0"));
  }

  @Test
  void swaggerUi_ShouldBeAvailable() throws Exception {
    mockMvc
        .perform(get("/swagger-ui.html"))
        .andExpect(status().is3xxRedirection());
  }
}
