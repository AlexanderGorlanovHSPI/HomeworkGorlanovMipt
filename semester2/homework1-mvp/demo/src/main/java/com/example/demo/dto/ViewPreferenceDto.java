package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Текущий режим отображения задач")
public class ViewPreferenceDto {
  @Schema(example = "compact", allowableValues = {"compact", "detailed"})
  private String viewPreference;

  public ViewPreferenceDto() {}

  public ViewPreferenceDto(String viewPreference) {
    this.viewPreference = viewPreference;
  }

  public String getViewPreference() {
    return viewPreference;
  }

  public void setViewPreference(String viewPreference) {
    this.viewPreference = viewPreference;
  }
}
