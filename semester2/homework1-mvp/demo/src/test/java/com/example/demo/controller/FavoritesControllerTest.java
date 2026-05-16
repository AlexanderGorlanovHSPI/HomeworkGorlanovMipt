package com.example.demo.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FavoritesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @Test
  void addToFavorites_WithExistingTask_ShouldReturnOk() throws Exception {
    when(taskService.existsById(1L)).thenReturn(true);

    mockMvc.perform(post("/api/favorites/1"))
        .andExpect(status().isOk());

    verify(taskService, times(1)).existsById(1L);
  }

  @Test
  void addToFavorites_WithNonExistingTask_ShouldReturnNotFound() throws Exception {
    when(taskService.existsById(999L)).thenReturn(false);

    mockMvc.perform(post("/api/favorites/999"))
        .andExpect(status().isNotFound());

    verify(taskService, times(1)).existsById(999L);
  }

  @Test
  void getFavorites_ShouldReturnFavoriteTasksFromSession() throws Exception {
    Task task = new Task(1L, "Task 1", "Description", false);
    when(taskService.existsById(1L)).thenReturn(true);
    when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

    MvcResult addResult = mockMvc.perform(post("/api/favorites/1"))
        .andExpect(status().isOk())
        .andReturn();

    MockHttpSession session = (MockHttpSession) addResult.getRequest().getSession(false);

    mockMvc.perform(get("/api/favorites").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Task 1"));
  }

  @Test
  void removeFromFavorites_ShouldDeleteTaskIdFromSession() throws Exception {
    Task task = new Task(1L, "Task 1", "Description", false);
    when(taskService.existsById(1L)).thenReturn(true);
    when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

    MvcResult addResult = mockMvc.perform(post("/api/favorites/1"))
        .andExpect(status().isOk())
        .andReturn();
    MockHttpSession session = (MockHttpSession) addResult.getRequest().getSession(false);

    mockMvc.perform(delete("/api/favorites/1").session(session))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/favorites").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
