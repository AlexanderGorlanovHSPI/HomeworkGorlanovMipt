package com.example.homework4.external;

import com.example.homework4.dto.TaskCreateRequest;
import com.example.homework4.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {
    private final AtomicLong idSeq = new AtomicLong(0);
    private final Map<Long, TaskResponse> store = new ConcurrentHashMap<>();

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        long id = idSeq.incrementAndGet();
        TaskResponse task = new TaskResponse(id, request.title(), request.description(),
                request.completed() != null ? request.completed() : false);
        store.put(id, task);
        return ResponseEntity.created(URI.create("/external/v1/tasks/" + id)).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        TaskResponse task = store.get(id);
        if (task == null) {
            return notFound("Task with id=" + id + " not found");
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit
    ) {
        List<TaskResponse> tasks = new ArrayList<>(store.values());
        tasks.sort(Comparator.comparing(TaskResponse::id));

        if (completed != null) {
            tasks = tasks.stream().filter(t -> completed.equals(t.completed())).toList();
        }

        if (limit != null && limit >= 0 && limit < tasks.size()) {
            tasks = tasks.subList(0, limit);
        }

        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        TaskResponse removed = store.remove(id);
        if (removed == null) {
            return notFound("Task with id=" + id + " not found");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(Duration.ofSeconds(5));
                yield ResponseEntity.ok(Map.of("status", "slow-response"));
            }
            case "500" -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Synthetic external 500 error");
                pd.setTitle("External Error");
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
            }
            case "429" -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS,
                        "Synthetic external rate limit");
                pd.setTitle("Too Many Requests");
                yield ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .header(HttpHeaders.RETRY_AFTER, "3")
                        .body(pd);
            }
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>502 Bad Gateway</h1></body></html>");
            default -> ResponseEntity.badRequest().body(Map.of("error", "Unsupported mode"));
        };
    }

    private ResponseEntity<ProblemDetail> notFound(String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
        problemDetail.setTitle("Task not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
}
