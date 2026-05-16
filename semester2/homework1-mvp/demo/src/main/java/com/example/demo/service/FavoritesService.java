package com.example.demo.service;

import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Task;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoritesService {

  private static final String FAVORITE_TASK_IDS_SESSION_KEY = "favoriteTaskIds";
  private final TaskService taskService;

  @Autowired
  public FavoritesService(TaskService taskService) {
    this.taskService = taskService;
  }

  public void addToFavorites(Long taskId, HttpSession session) {
    if (!taskService.existsById(taskId)) {
      throw new TaskNotFoundException(taskId);
    }

    Set<Long> favoriteTaskIds = getOrCreateFavoriteTaskIds(session);
    favoriteTaskIds.add(taskId);
    session.setAttribute(FAVORITE_TASK_IDS_SESSION_KEY, favoriteTaskIds);
  }

  public void removeFromFavorites(Long taskId, HttpSession session) {
    Set<Long> favoriteTaskIds = getOrCreateFavoriteTaskIds(session);
    favoriteTaskIds.remove(taskId);
    session.setAttribute(FAVORITE_TASK_IDS_SESSION_KEY, favoriteTaskIds);
  }

  public Set<Long> getFavoriteTaskIds(HttpSession session) {
    return new LinkedHashSet<>(getOrCreateFavoriteTaskIds(session));
  }

  public List<Task> getFavoriteTasks(HttpSession session) {
    return getOrCreateFavoriteTaskIds(session).stream()
        .map(taskService::getTaskById)
        .flatMap(Optional::stream)
        .toList();
  }

  @SuppressWarnings("unchecked")
  private Set<Long> getOrCreateFavoriteTaskIds(HttpSession session) {
    Object value = session.getAttribute(FAVORITE_TASK_IDS_SESSION_KEY);

    if (value instanceof Set<?> favoriteTaskIds) {
      return (Set<Long>) favoriteTaskIds;
    }

    Set<Long> ids = new LinkedHashSet<>();
    session.setAttribute(FAVORITE_TASK_IDS_SESSION_KEY, ids);
    return ids;
  }
}
