package com.example.homework4.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DocsController {
    @GetMapping("/docs")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE')")
    public ResponseEntity<Map<String, String>> docs() {
        return ResponseEntity.ok(Map.of("message", "Protected docs for users with READ_PRIVILEGE"));
    }
}
