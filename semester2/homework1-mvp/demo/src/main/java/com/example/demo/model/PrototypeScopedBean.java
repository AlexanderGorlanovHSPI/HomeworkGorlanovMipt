package com.example.demo.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Бин с областью видимости "prototype".
 *
 * <p>Создает новый экземпляр бина при каждом запросе из контекста Spring. Используется для
 * демонстрации prototype-scope и генерации уникальных идентификаторов.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see Scope
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

  private final String beanId;
  private static int instanceCounter = 0;
  private final int instanceNumber;

  /**
   * Конструктор, создающий новый экземпляр бина с уникальными идентификаторами.
   *
   * <p>При создании автоматически увеличивает счетчик экземпляров и выводит сообщение в консоль.
   */
  public PrototypeScopedBean() {
    this.beanId = UUID.randomUUID().toString();
    this.instanceNumber = ++instanceCounter;
    System.out.println(
        "【PrototypeScope】Создан новый PrototypeScopedBean #" + instanceNumber + " с ID: " + beanId);
  }

  /**
   * Генерирует уникальный идентификатор для задачи.
   *
   * @return сгенерированный ID задачи (число от 0 до 9999)
   */
  public Long generateTaskId() {
    long id = Math.abs(UUID.randomUUID().getMostSignificantBits()) % 10000;
    System.out.println("【PrototypeScope #" + instanceNumber + "】Сгенерирован ID задачи: " + id);
    return id;
  }

  /**
   * Возвращает уникальный идентификатор бина.
   *
   * @return UUID бина в виде строки
   */
  public String getBeanId() {
    return beanId;
  }

  /**
   * Возвращает порядковый номер экземпляра бина.
   *
   * @return номер экземпляра (начинается с 1)
   */
  public int getInstanceNumber() {
    return instanceNumber;
  }
}
