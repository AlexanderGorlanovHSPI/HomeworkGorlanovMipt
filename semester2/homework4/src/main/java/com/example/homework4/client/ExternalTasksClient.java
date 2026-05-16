package com.example.homework4.client;

import com.example.homework4.dto.TaskCreateRequest;
import com.example.homework4.dto.TaskResponse;
import com.example.homework4.exception.ExternalApiException;
import com.example.homework4.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.List;

@Component
public class ExternalTasksClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);

    private final RestClient externalRestClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalRestClient, ObjectMapper objectMapper) {
        this.externalRestClient = externalRestClient;
        this.objectMapper = objectMapper;
    }

    public CreatedTaskResult createTask(TaskCreateRequest request) {
        try {
            ResponseEntity<TaskResponse> response = externalRestClient.post()
                    .uri("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(TaskResponse.class);

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new ExternalApiException("External API returned unexpected status on create: " + response.getStatusCode());
            }

            URI location = response.getHeaders().getLocation();
            if (location == null) {
                throw new ExternalApiException("External API did not provide Location header for created task");
            }

            return new CreatedTaskResult(response.getBody(), location.toString());
        } catch (RestClientResponseException ex) {
            throw mapError(ex, "create task");
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout on create task", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Failed to create task in external API", ex);
        }
    }

    public TaskResponse getTask(Long id) {
        try {
            return externalRestClient.get()
                    .uri("/tasks/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(TaskResponse.class);
        } catch (RestClientResponseException ex) {
            throw mapError(ex, "get task");
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout on get task", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Failed to get task from external API", ex);
        }
    }

    public List<TaskResponse> listTasks(Boolean completed, Integer limit) {
        try {
            return externalRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/tasks")
                            .queryParamIfPresent("completed", java.util.Optional.ofNullable(completed))
                            .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException ex) {
            throw mapError(ex, "list tasks");
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout on list tasks", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Failed to list tasks from external API", ex);
        }
    }

    public void deleteTask(Long id) {
        try {
            ResponseEntity<Void> response = externalRestClient.delete()
                    .uri("/tasks/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new ExternalApiException("External API returned unexpected status on delete: " + response.getStatusCode());
            }
        } catch (RestClientResponseException ex) {
            throw mapError(ex, "delete task");
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout on delete task", ex);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Failed to delete task from external API", ex);
        }
    }

    private RuntimeException mapError(RestClientResponseException ex, String action) {
        HttpStatusCode statusCode = ex.getStatusCode();
        String responseBody = ex.getResponseBodyAsString();

        if (statusCode.value() == 404) {
            String detail = extractProblemDetail(responseBody);
            return new TaskNotFoundException(detail != null ? detail : "Task not found");
        }

        if (statusCode.is5xxServerError()) {
            logLimitedUnexpectedBody(ex.getResponseHeaders(), responseBody);
            return new ExternalApiException("External API 5xx error while trying to " + action);
        }

        if (statusCode.value() == 429) {
            return new ExternalApiException("External API rate limit exceeded while trying to " + action);
        }

        return new ExternalApiException("External API error while trying to " + action + ": HTTP " + statusCode.value());
    }

    private String extractProblemDetail(String body) {
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode detail = node.get("detail");
            return detail != null ? detail.asText() : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private void logLimitedUnexpectedBody(HttpHeaders headers, String body) {
        MediaType contentType = headers != null ? headers.getContentType() : null;
        String limitedBody = body == null ? "" : body.substring(0, Math.min(body.length(), 500));
        log.warn("External API error contentType={} body={}", contentType, limitedBody);
    }

    public record CreatedTaskResult(TaskResponse task, String location) {
    }
}
