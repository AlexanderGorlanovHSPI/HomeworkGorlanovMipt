package patterns;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MetricableDecorator extends DataServiceDecorator {
    private final MetricService metricService;

    public MetricableDecorator(DataService wrapped) {
        super(wrapped);
        this.metricService = new MetricService();
    }

    public MetricableDecorator(DataService wrapped, MetricService metricService) {
        super(wrapped);
        this.metricService = metricService;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        Instant start = Instant.now();
        try {
            return wrapped.findDataByKey(key);
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            metricService.sendMetric("findDataByKey", duration);
        }
    }

    @Override
    public void saveData(String key, String data) {
        Instant start = Instant.now();
        try {
            wrapped.saveData(key, data);
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            metricService.sendMetric("saveData", duration);
        }
    }

    @Override
    public boolean deleteData(String key) {
        Instant start = Instant.now();
        try {
            return wrapped.deleteData(key);
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            metricService.sendMetric("deleteData", duration);
        }
    }

    public static class MetricService {
        public void sendMetric(String methodName, Duration duration) {
            System.out.println("[METRIC] Метод " + methodName + " выполнялся: " + duration.toMillis() + "ms");
        }
    }
}