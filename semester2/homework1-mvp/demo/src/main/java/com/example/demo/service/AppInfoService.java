package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Сервис для управления задачами. Содержит бизнес-логику приложения.
 *
 * <p>Использует {@link TaskRepository} для доступа к данным и поддерживает кэширование задач через
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see Task
 * @see TaskRepository
 */
@Service
public class AppInfoService {

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  @Value("${app.description:No description}")
  private String appDescription;

  @Value("${server.port}")
  private int serverPort;

  @Value("${java.version}")
  private String javaVersion;

  @Value("${user.dir}")
  private String workingDirectory;

  @PostConstruct
  public void printAppInfo() {
    System.out.println("\n=== Информация о приложении ===");
    System.out.println("App Name: " + appName);
    System.out.println("App Version: " + appVersion);
    System.out.println("Description: " + appDescription);
    System.out.println("Server Port: " + serverPort);
    System.out.println("Java Version: " + javaVersion);
    System.out.println("Working Directory: " + workingDirectory);
    System.out.println("==============================\n");
  }

  public String getAppInfo() {
    return String.format("%s v%s - %s (port: %d)", appName, appVersion, appDescription, serverPort);
  }
}
