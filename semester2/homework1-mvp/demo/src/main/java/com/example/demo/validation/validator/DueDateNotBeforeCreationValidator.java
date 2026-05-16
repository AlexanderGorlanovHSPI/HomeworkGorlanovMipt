package com.example.demo.validation.validator;

import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import com.example.demo.validation.annotation.DueDateNotBeforeCreation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class DueDateNotBeforeCreationValidator
    implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

  private final TaskService taskService;
  private final HttpServletRequest request;

  @Autowired
  public DueDateNotBeforeCreationValidator(TaskService taskService, HttpServletRequest request) {
    this.taskService = taskService;
    this.request = request;
  }

  @Override
  public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
    if (dto == null || dto.getDueDate() == null) {
      return true;
    }

    Long taskId = resolveTaskIdFromPath();
    if (taskId == null) {
      return true;
    }

    Optional<Task> existingTask = taskService.getTaskById(taskId);
    if (existingTask == null || existingTask.isEmpty() || existingTask.get().getCreatedAt() == null) {
      return true;
    }

    boolean valid = !dto.getDueDate().isBefore(existingTask.get().getCreatedAt().toLocalDate());
    if (!valid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "dueDate must be on or after task creation date")
          .addPropertyNode("dueDate")
          .addConstraintViolation();
    }
    return valid;
  }

  @SuppressWarnings("unchecked")
  private Long resolveTaskIdFromPath() {
    Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (!(attribute instanceof Map<?, ?> vars)) {
      return null;
    }

    Object idValue = vars.get("id");
    if (idValue == null) {
      return null;
    }

    try {
      return Long.valueOf(String.valueOf(idValue));
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
