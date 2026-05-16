package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Работа с избранными задачами в сессии")
public class FavoritesController {

  private final FavoritesService favoritesService;
  private final TaskMapper taskMapper;

  @Autowired
  public FavoritesController(FavoritesService favoritesService, TaskMapper taskMapper) {
    this.favoritesService = favoritesService;
    this.taskMapper = taskMapper;
  }

  @PostMapping("/{taskId}")
  @Operation(summary = "Добавить задачу в избранное")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Задача добавлена в избранное"),
      @ApiResponse(
          responseCode = "404",
          description = "Задача не найдена",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> addToFavorites(@PathVariable Long taskId, HttpSession session) {
    favoritesService.addToFavorites(taskId, session);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{taskId}")
  @Operation(summary = "Удалить задачу из избранного")
  @ApiResponse(responseCode = "204", description = "Задача удалена из избранного")
  public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
    favoritesService.removeFromFavorites(taskId, session);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Получить избранные задачи")
  @ApiResponse(
      responseCode = "200",
      description = "Список избранных задач",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class))))
  public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
    List<TaskResponseDto> favorites =
        taskMapper.toResponseDtoList(favoritesService.getFavoriteTasks(session));
    return ResponseEntity.ok(favorites);
  }
}
