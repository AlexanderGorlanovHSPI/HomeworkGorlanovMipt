package com.example.homework4.api;

import com.example.homework4.client.ExternalTasksClient;
import com.example.homework4.dto.CreatedTaskResponse;
import com.example.homework4.dto.DeleteTaskResponse;
import com.example.homework4.dto.TaskCreateRequest;
import com.example.homework4.dto.TaskResponse;
import com.example.homework4.service.TasksGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {
    private final TasksGatewayService tasksGatewayService;

    public TasksGatewayController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<CreatedTaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        ExternalTasksClient.CreatedTaskResult result = tasksGatewayService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, result.location())
                .body(new CreatedTaskResponse(result.task(), result.location()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(tasksGatewayService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(tasksGatewayService.listTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        boolean deleted = tasksGatewayService.deleteTask(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new DeleteTaskResponse("Delete request accepted in fallback mode"));
    }
}
