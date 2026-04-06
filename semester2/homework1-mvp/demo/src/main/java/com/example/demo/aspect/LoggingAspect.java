package com.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для логирования выполнения методов сервисов.
 * Использует {@link Around} advice для замера времени выполнения
 * и логирования входных параметров и результатов.
 *
 * @author Gorlanov Alexander
 * @version 1.0
 * @see org.aspectj.lang.annotation.Aspect
 */
@Aspect
@Component
public class LoggingAspect {

  /**
   * аспект будет преминятся ко всем сервисам проекта
   */
@Pointcut("execution(* com.example.demo.service.*.*(..))")
  public void serviceMethods() {}

  /**
   * оборачивает выполнение методов сервисов для логирования их работы.
   *
   * @param joinPoint объект содержащий информацию о вызванном методе(класс его содержащий, имя метода, аргументы)
   * @return  the object
   * @throws Throwable пробрасывает оригинальное исключение, логируя его
   */
@Around("serviceMethods()")
  public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    System.out.println(
        "【AOP】Начало выполнения метода "
            + className
            + "."
            + methodName
            + " с аргументами: "
            + Arrays.toString(args));

    long startTime = System.currentTimeMillis();

    try {
      Object result = joinPoint.proceed();

      long executionTime = System.currentTimeMillis() - startTime;

      System.out.println(
          "【AOP】Метод "
              + className
              + "."
              + methodName
              + " завершен за "
              + executionTime
              + "ms. Результат: "
              + (result != null ? result : "void"));

      return result;

    } catch (Throwable throwable) {
      long executionTime = System.currentTimeMillis() - startTime;
      System.out.println(
          "【AOP】Метод "
              + className
              + "."
              + methodName
              + " выбросил исключение через "
              + executionTime
              + "ms: "
              + throwable.getMessage());
      throw throwable;
    }
  }
}
