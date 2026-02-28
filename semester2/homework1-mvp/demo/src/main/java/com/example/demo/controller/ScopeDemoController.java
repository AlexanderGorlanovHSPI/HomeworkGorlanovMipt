package com.example.demo.controller;

import com.example.demo.model.PrototypeScopedBean;
import com.example.demo.model.RequestScopedBean;
import com.example.demo.model.Task;
import com.example.demo.service.AppInfoService;
import com.example.demo.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для демонстрации различных областей видимости (scopes) бинов в Spring.
 *
 * <p>Предоставляет эндпоинты для демонстрации работы request-scoped и prototype-scoped бинов, а
 * также получения информации о приложении.
 *
 * @author Alexander Gorlanov
 * @version 1.0
 * @see RequestScopedBean
 * @see PrototypeScopedBean
 * @see AppInfoService
 */
@RestController
@RequestMapping("/api/demo")
public class ScopeDemoController {

  private final RequestScopedBean requestScopedBean;
  private final ObjectFactory<PrototypeScopedBean> prototypeBeanFactory;
  private final AppInfoService appInfoService;

  /**
   * Конструктор с внедрением зависимостей.
   *
   * @param requestScopedBean бин с областью видимости "request" (создается на каждый HTTP-запрос)
   * @param prototypeBeanFactory фабрика для получения prototype-бинов (каждый вызов создает новый
   *     экземпляр)
   * @param appInfoService сервис информации о приложении
   */
  @Autowired
  public ScopeDemoController(
      RequestScopedBean requestScopedBean,
      ObjectFactory<PrototypeScopedBean> prototypeBeanFactory,
      AppInfoService appInfoService) {
    this.requestScopedBean = requestScopedBean;
    this.prototypeBeanFactory = prototypeBeanFactory;
    this.appInfoService = appInfoService;
  }

  /**
   * Демонстрирует работу request-scoped бина.
   *
   * <p>Для каждого HTTP-запроса создается новый экземпляр RequestScopedBean. Метод устанавливает
   * информацию о клиенте и возвращает данные бина.
   *
   * @param request объект HttpServletRequest для получения информации о клиенте
   * @return строковое представление состояния request-scoped бина
   */
  @GetMapping("/request-scope")
  public String demoRequestScope(HttpServletRequest request) {
    requestScopedBean.setClientInfo(request.getRemoteAddr());

    return "Request Scope Demo:\n"
        + "Request ID: "
        + requestScopedBean.getRequestId()
        + "\n"
        + "Start Time: "
        + requestScopedBean.getFormattedStartTime()
        + "\n"
        + "Bean: "
        + requestScopedBean;
  }

  /**
   * Демонстрирует работу prototype-scoped бинов.
   *
   * <p>Через ObjectFactory запрашиваются два экземпляра PrototypeScopedBean. Каждый вызов
   * getObject() создает новый независимый экземпляр бина.
   *
   * @return строковое представление, показывающее различия между двумя экземплярами бина
   */
  @GetMapping("/prototype-scope")
  public String demoPrototypeScope() {
    PrototypeScopedBean bean1 = prototypeBeanFactory.getObject();
    PrototypeScopedBean bean2 = prototypeBeanFactory.getObject();

    Long taskId1 = bean1.generateTaskId();
    Long taskId2 = bean2.generateTaskId();

    return "Prototype Scope Demo:\n"
        + "Bean1 #"
        + bean1.getInstanceNumber()
        + " ID: "
        + bean1.getBeanId()
        + "\n"
        + "Bean2 #"
        + bean2.getInstanceNumber()
        + " ID: "
        + bean2.getBeanId()
        + "\n"
        + "Task IDs: "
        + taskId1
        + ", "
        + taskId2
        + "\n"
        + "Same bean? "
        + (bean1 == bean2);
  }

  /**
   * Получает информацию о приложении.
   *
   * @return строка с названием, версией, описанием и портом приложения
   */
  @GetMapping("/info")
  public String getAppInfo() {
    return appInfoService.getAppInfo();
  }
}
