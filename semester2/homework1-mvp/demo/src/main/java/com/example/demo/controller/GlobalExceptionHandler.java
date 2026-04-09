package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.exception.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final Environment environment;

  public GlobalExceptionHandler(Environment environment) {
    this.environment = environment;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    Map<String, Object> fieldErrors = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Validation failed",
        request.getRequestURI(),
        Map.of("fieldErrors", fieldErrors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, HttpServletRequest request) {

    Map<String, Object> violations =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    violation -> violation.getMessage(),
                    (oldValue, newValue) -> newValue,
                    LinkedHashMap::new));

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Constraint violation",
        request.getRequestURI(),
        Map.of("violations", violations));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex, HttpServletRequest request) {

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        ex.getMessage(),
        request.getRequestURI(),
        Map.of("parameter", ex.getParameterName()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Malformed request body",
        request.getRequestURI(),
        Map.of("reason", ex.getMostSpecificCause().getMessage()));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpServletRequest request) {

    return buildResponse(
        HttpStatus.NOT_FOUND,
        "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
        request.getRequestURI(),
        Map.of());
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleTaskNotFoundException(
      TaskNotFoundException ex, HttpServletRequest request) {

    return buildResponse(
        HttpStatus.NOT_FOUND,
        ex.getMessage(),
        request.getRequestURI(),
        Map.of("type", "TASK_NOT_FOUND"));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleNoSuchElementException(
      NoSuchElementException ex, HttpServletRequest request) {

    return buildResponse(
        HttpStatus.NOT_FOUND,
        ex.getMessage(),
        request.getRequestURI(),
        Map.of("type", "NOT_FOUND"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        ex.getMessage(),
        request.getRequestURI(),
        Map.of("type", "BAD_REQUEST"));
  }

  @ExceptionHandler(Exception.class)
  public org.springframework.http.ResponseEntity<ErrorResponse> handleException(
      Exception ex, HttpServletRequest request) {

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("type", ex.getClass().getSimpleName());

    if (!isProduction()) {
      String stackTrace =
          java.util.Arrays.stream(ex.getStackTrace())
              .limit(20)
              .map(StackTraceElement::toString)
              .collect(Collectors.joining("\n"));
      details.put("stackTrace", stackTrace);
    }

    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error",
        request.getRequestURI(),
        details);
  }

  private org.springframework.http.ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String message, String path, Map<String, Object> details) {

    ErrorResponse response =
        new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path,
            details);

    return org.springframework.http.ResponseEntity.status(status).body(response);
  }

  private boolean isProduction() {
    return java.util.Arrays.stream(environment.getActiveProfiles())
        .anyMatch("prod"::equalsIgnoreCase);
  }
}
