package patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachingDecorator extends DataServiceDecorator {
    private final Map<String, String> cache = new HashMap<>();

    public CachingDecorator(DataService wrapped) {
        super(wrapped);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        if (cache.containsKey(key)) {
            return Optional.of(cache.get(key));
        }

        Optional<String> result = wrapped.findDataByKey(key);
        result.ifPresent(data -> cache.put(key, data));
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        wrapped.saveData(key, data);
        cache.put(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        boolean result = wrapped.deleteData(key);
        cache.remove(key);
        return result;
    }

    public int getCacheSize() {
        return cache.size();
    }
}