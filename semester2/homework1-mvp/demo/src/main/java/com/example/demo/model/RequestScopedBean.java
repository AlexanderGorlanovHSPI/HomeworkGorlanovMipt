package com.example.demo.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Бин с областью видимости "request".
 *
 * <p>Создает новый экземпляр бина для каждого HTTP-запроса. Использует прокси-режим {@link
 * ScopedProxyMode#TARGET_CLASS} для внедрения в компоненты с более длительным жизненным циклом
 * (например, синглтоны).
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see Scope
 * @see WebApplicationContext#SCOPE_REQUEST
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {

  private final String requestId;
  private final LocalDateTime requestStartTime;
  private final String clientInfo;

  /**
   * Конструктор, создающий новый экземпляр бина для HTTP-запроса.
   *
   * <p>Инициализирует уникальный ID запроса, время старта и выводит сообщение в консоль.
   */
  public RequestScopedBean() {
    this.requestId = UUID.randomUUID().toString().substring(0, 8);
    this.requestStartTime = LocalDateTime.now();
    this.clientInfo = "Unknown";
    System.out.println("【RequestScope】Новый RequestScopedBean создан! ID: " + requestId);
  }

  /**
   * Возвращает уникальный идентификатор запроса.
   *
   * @return первые 8 символов UUID запроса
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * Возвращает время начала запроса в отформатированном виде.
   *
   * @return время в формате ISO (например, "14:30:45.123")
   */
  public String getFormattedStartTime() {
    return requestStartTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
  }

  /**
   * Устанавливает информацию о клиенте для текущего запроса.
   *
   * @param clientInfo IP-адрес или другая информация о клиенте
   */
  public void setClientInfo(String clientInfo) {
    System.out.println(
        "【RequestScope】Установка clientInfo для request " + requestId + ": " + clientInfo);
  }

  /**
   * Возвращает строковое представление request-scoped бина.
   *
   * @return строка с ID запроса и временем старта
   */
  @Override
  public String toString() {
    return "RequestScopedBean{"
        + "requestId='"
        + requestId
        + '\''
        + ", startTime="
        + getFormattedStartTime()
        + '}';
  }
}
