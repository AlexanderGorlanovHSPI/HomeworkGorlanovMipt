package com.example.demo.config;

import com.example.demo.repository.StubTaskRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Конфигурационный класс приложения. Определяет бины, не помеченные стереотипными аннотациями.
 *
 * <p>Создает бин {@link StubTaskRepository} через {@link Bean}.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {
  /**
   * Выполняется перед инициализацией бина для каждого созданного, сразу после внедрения
   * зависимостей. Логирует их информацию(имя и класс)
   *
   * @param bean созданный экземпляр бина (еще до инициализации)
   * @param beanName имя бина в Spring-контексте
   * @returnисходный экземпляр бина (или его модифицированную версию, если требуется обертка/замена)
   * @throws BeansException если произошла ошибка при обработке бина
   */
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      System.out.println(
          "【BeanPostProcessor:before】"
              + beanName
              + " - "
              + bean.getClass().getSimpleName()
              + " - перед инициализацией");
    }
    return bean;
  }

  /**
   * Выполняется после инициализации бина, но до того как он станет доступным для использования
   * логирует информацию о бинах домена задач, но уже после их полной инициализации.
   *
   * @param bean инициализированный экземпляр бина
   * @param beanName имя бина в Spring-контексте
   * @returnисходный экземпляр бина (или его модифицированную версию, если требуется обертка/замена)
   * @throws BeansException если произошла ошибка при обработке бина
   */
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      System.out.println(
          "【BeanPostProcessor:after】"
              + beanName
              + " - "
              + bean.getClass().getSimpleName()
              + " - после инициализации");
    }
    return bean;
  }
}
