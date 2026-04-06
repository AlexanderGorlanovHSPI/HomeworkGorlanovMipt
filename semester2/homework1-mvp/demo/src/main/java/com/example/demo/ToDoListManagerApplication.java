package com.example.demo;

import com.example.demo.service.TaskStatisticsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ToDoListManagerApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context =
        SpringApplication.run(ToDoListManagerApplication.class, args);
    TaskStatisticsService statisticsService = context.getBean(TaskStatisticsService.class);
    statisticsService.compareRepositories();
  }
}
