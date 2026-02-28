package patterns;

import java.util.Optional;

public class ValidationDecorator extends DataServiceDecorator {

    public ValidationDecorator(DataService wrapped) {
        super(wrapped);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        validateKey(key);
        return wrapped.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        validateKey(key);
        validateData(data);
        wrapped.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        validateKey(key);
        return wrapped.deleteData(key);
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Ключ не может быть пустым");
        }
        if (key.length() > 100) {
            throw new IllegalArgumentException("Ключ слишком длинный (макс. 100 символов)");
        }
    }

    private void validateData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Данные не могут быть null");
        }
        if (data.length() > 1000) {
            throw new IllegalArgumentException("Данные слишком большие (макс. 1000 символов)");
        }
    }
}