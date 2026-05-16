package com.example.homework4.service;

import com.example.homework4.client.ExternalTasksClient;
import com.example.homework4.dto.TaskCreateRequest;
import com.example.homework4.dto.TaskResponse;
import com.example.homework4.exception.ExternalApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TasksGatewayService {
    private static final Logger log = LoggerFactory.getLogger(TasksGatewayService.class);

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public ExternalTasksClient.CreatedTaskResult createTask(TaskCreateRequest request) {
        return externalTasksClient.createTask(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public TaskResponse getTask(Long id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
    public List<TaskResponse> listTasks(Boolean completed, Integer limit) {
        return externalTasksClient.listTasks(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public boolean deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
        return true;
    }

    public ExternalTasksClient.CreatedTaskResult createTaskFallback(TaskCreateRequest request, Throwable throwable) {
        handleRateLimit(throwable);
        log.warn("Fallback createTask: {}", throwable.getMessage());
        TaskResponse fallbackTask = new TaskResponse(-1L, request.title(), request.description(), false);
        return new ExternalTasksClient.CreatedTaskResult(fallbackTask, "fallback://unavailable");
    }

    public TaskResponse getTaskFallback(Long id, Throwable throwable) {
        handleRateLimit(throwable);
        log.warn("Fallback getTask id={}: {}", id, throwable.getMessage());
        return new TaskResponse(id, "Fallback task", "External API unavailable", false);
    }

    public List<TaskResponse> listTasksFallback(Boolean completed, Integer limit, Throwable throwable) {
        handleRateLimit(throwable);
        log.warn("Fallback listTasks: {}", throwable.getMessage());
        return List.of(new TaskResponse(-1L, "Fallback task list", "External API unavailable", false));
    }

    public boolean deleteTaskFallback(Long id, Throwable throwable) {
        handleRateLimit(throwable);
        log.warn("Fallback deleteTask id={}: {}", id, throwable.getMessage());
        return false;
    }

    private void handleRateLimit(Throwable throwable) {
        if (throwable instanceof RequestNotPermitted) {
            throw new ExternalApiException("Rate limiter rejected request", throwable);
        }
    }
}
