package patterns;

import java.util.Optional;

public class LoggingDecorator extends DataServiceDecorator {

    public LoggingDecorator(DataService wrapped) {
        super(wrapped);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        System.out.println("[LOG] Поиск данных по ключу: " + key);
        Optional<String> result = wrapped.findDataByKey(key);
        System.out.println("[LOG] Результат поиска: " + (result.isPresent() ? "найден" : "не найден"));
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        System.out.println("[LOG] Сохранение данных. Ключ: " + key + ", Данные: " + data);
        wrapped.saveData(key, data);
        System.out.println("[LOG] Данные сохранены успешно");
    }

    @Override
    public boolean deleteData(String key) {
        System.out.println("[LOG] Удаление данных по ключу: " + key);
        boolean result = wrapped.deleteData(key);
        System.out.println("[LOG] Результат удаления: " + (result ? "успешно" : "ключ не найден"));
        return result;
    }
}