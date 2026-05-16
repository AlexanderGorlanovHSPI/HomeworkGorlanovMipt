package com.example.homework4.api;

import com.example.homework4.dto.LoginRequest;
import com.example.homework4.dto.LoginResponse;
import com.example.homework4.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.security.pepper}")
    private String pepper;

    public AuthController(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        boolean valid = passwordEncoder.matches(request.password() + pepper, userDetails.getPassword());
        if (!valid) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}
