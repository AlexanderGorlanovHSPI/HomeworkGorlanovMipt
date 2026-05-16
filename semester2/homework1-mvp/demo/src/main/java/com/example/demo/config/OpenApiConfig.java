package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI toDoOpenApi(
      @Value("${app.api.title:To-Do List API}") String title,
      @Value("${app.api.version:2.0.0}") String version,
      @Value("${app.api.description:API для управления задачами, вложениями и пользовательскими настройками}") String description,
      @Value("${app.api.contact.name:Alexander Gorlanov}") String contactName,
      @Value("${app.api.contact.email:alexander@example.com}") String contactEmail,
      @Value("${app.api.contact.url:https://example.com}") String contactUrl) {

    return new OpenAPI()
        .info(
            new Info()
                .title(title)
                .version(version)
                .description(description)
                .contact(new Contact().name(contactName).email(contactEmail).url(contactUrl)));
  }
}
