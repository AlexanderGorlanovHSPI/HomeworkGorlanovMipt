package com.example.demo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PreferencesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getViewPreference_WithoutCookie_ShouldReturnDefaultAndSetCookie() throws Exception {
    mockMvc.perform(get("/api/preferences/view"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.viewPreference").value("detailed"))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("viewPreference=detailed")));
  }

  @Test
  void getViewPreference_WithCookie_ShouldReturnCookieValue() throws Exception {
    mockMvc.perform(get("/api/preferences/view").cookie(new Cookie("viewPreference", "compact")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.viewPreference").value("compact"));
  }

  @Test
  void setViewPreference_WithValidMode_ShouldReturnOkAndSetCookie() throws Exception {
    mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.viewPreference").value("compact"))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("viewPreference=compact")));
  }

  @Test
  void setViewPreference_WithInvalidMode_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(post("/api/preferences/view").param("mode", "invalid"))
        .andExpect(status().isBadRequest());
  }
}
