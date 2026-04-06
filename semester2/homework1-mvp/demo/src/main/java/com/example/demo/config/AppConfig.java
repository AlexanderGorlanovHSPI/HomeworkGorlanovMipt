package com.example.demo.config;

import com.example.demo.repository.StubTaskRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс приложения. Определяет бины, не помеченные стереотипными аннотациями.
 *
 * <p>Создает бин {@link StubTaskRepository} через {@link Bean}.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 */
@Configuration
public class AppConfig {

  /**
   * Создает бин StubTaskRepository через @Bean. Это альтернативный способ создания бина (не
   * через @Repository)
   */
  @Bean
  public TaskRepository stubTaskRepository() {
    return new StubTaskRepository();
  }
}
