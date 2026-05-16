package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.ViewPreferenceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "Пользовательские настройки в cookie")
public class PreferencesController {

  private static final String VIEW_PREFERENCE_COOKIE = "viewPreference";
  private static final String DEFAULT_VIEW_PREFERENCE = "detailed";
  private static final Set<String> ALLOWED_VIEW_MODES = Set.of("compact", "detailed");

  @GetMapping("/view")
  @Operation(summary = "Получить режим отображения задач")
  @ApiResponse(
      responseCode = "200",
      description = "Текущая настройка отображения",
      content = @Content(schema = @Schema(implementation = ViewPreferenceDto.class)))
  public ResponseEntity<ViewPreferenceDto> getViewPreference(
      @CookieValue(value = VIEW_PREFERENCE_COOKIE, required = false) String mode) {

    String normalizedMode = normalizeMode(mode);
    if (normalizedMode == null) {
      normalizedMode = DEFAULT_VIEW_PREFERENCE;
      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, buildCookie(normalizedMode).toString())
          .body(new ViewPreferenceDto(normalizedMode));
    }

    return ResponseEntity.ok(new ViewPreferenceDto(normalizedMode));
  }

  @PostMapping("/view")
  @Operation(summary = "Установить режим отображения задач")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Настройка сохранена",
          content = @Content(schema = @Schema(implementation = ViewPreferenceDto.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Невалидное значение режима",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ViewPreferenceDto> setViewPreference(
      @Parameter(description = "compact или detailed", example = "compact") @RequestParam String mode) {
    String normalizedMode = normalizeMode(mode);
    if (normalizedMode == null) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, buildCookie(normalizedMode).toString())
        .body(new ViewPreferenceDto(normalizedMode));
  }

  private String normalizeMode(String mode) {
    if (mode == null || mode.isBlank()) {
      return null;
    }

    String normalizedMode = mode.trim().toLowerCase(Locale.ROOT);
    return ALLOWED_VIEW_MODES.contains(normalizedMode) ? normalizedMode : null;
  }

  private ResponseCookie buildCookie(String mode) {
    return ResponseCookie.from(VIEW_PREFERENCE_COOKIE, mode)
        .path("/")
        .maxAge(Duration.ofDays(30))
        .sameSite("Lax")
        .httpOnly(false)
        .build();
  }
}
