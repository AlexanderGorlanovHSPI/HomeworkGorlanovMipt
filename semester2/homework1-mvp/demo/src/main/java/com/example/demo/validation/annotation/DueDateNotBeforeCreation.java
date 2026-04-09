package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.DueDateNotBeforeCreationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
public @interface DueDateNotBeforeCreation {
  String message() default "dueDate must not be before task creation date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
