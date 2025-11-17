package patterns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class DataServiceTest {
    private SimpleDataService simpleService;

    @BeforeEach
    void setUp() {
        simpleService = new SimpleDataService();
    }

    @Test
    void testSimpleDataService() {
        simpleService.saveData("key1", "value1");
        assertEquals(Optional.of("value1"), simpleService.findDataByKey("key1"));
        assertTrue(simpleService.deleteData("key1"));
        assertEquals(Optional.empty(), simpleService.findDataByKey("key1"));
    }

    @Test
    void testCachingDecorator() {
        CachingDecorator cachingService = new CachingDecorator(simpleService);

        cachingService.saveData("key1", "value1");
        assertEquals(1, cachingService.getCacheSize());

        assertEquals(Optional.of("value1"), cachingService.findDataByKey("key1"));

        cachingService.deleteData("key1");
        assertEquals(0, cachingService.getCacheSize());
    }

    @Test
    void testLoggingDecorator() {
        LoggingDecorator loggingService = new LoggingDecorator(simpleService);

        loggingService.saveData("test", "data");
        Optional<String> result = loggingService.findDataByKey("test");
        assertTrue(result.isPresent());
        assertTrue(loggingService.deleteData("test"));
    }

    @Test
    void testMetricableDecorator() {
        MetricableDecorator.MetricService mockMetricService = new MetricableDecorator.MetricService() {
            @Override
            public void sendMetric(String methodName, java.time.Duration duration) {
                assertNotNull(methodName);
                assertNotNull(duration);
                assertTrue(duration.toNanos() >= 0);
            }
        };

        MetricableDecorator metricService = new MetricableDecorator(simpleService, mockMetricService);

        metricService.saveData("key", "data");
        metricService.findDataByKey("key");
        metricService.deleteData("key");
    }

    @Test
    void testValidationDecorator() {
        ValidationDecorator validationService = new ValidationDecorator(simpleService);

        assertThrows(IllegalArgumentException.class, () -> {
            validationService.saveData("", "data");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            validationService.saveData(null, "data");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            validationService.saveData("validKey", null);
        });

        assertDoesNotThrow(() -> {
            validationService.saveData("validKey", "validData");
            Optional<String> result = validationService.findDataByKey("validKey");
            assertTrue(result.isPresent());
            assertTrue(validationService.deleteData("validKey"));
        });
    }

    @Test
    void testDecoratorChain() {
        DataService service = new ValidationDecorator(
                new MetricableDecorator(
                        new LoggingDecorator(
                                new CachingDecorator(
                                        new SimpleDataService()
                                )
                        )
                )
        );

        // Все операции должны работать корректно
        service.saveData("key1", "value1");
        service.saveData("key2", "value2");

        assertEquals(Optional.of("value1"), service.findDataByKey("key1"));
        assertEquals(Optional.of("value2"), service.findDataByKey("key2"));

        assertTrue(service.deleteData("key1"));
        assertEquals(Optional.empty(), service.findDataByKey("key1"));
    }

    @Test
    void testEdgeCases() {
        ValidationDecorator validationService = new ValidationDecorator(simpleService);

        assertThrows(IllegalArgumentException.class, () -> {
            validationService.findDataByKey("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            validationService.deleteData(null);
        });

        assertEquals(Optional.empty(), validationService.findDataByKey("nonexistent"));

        assertFalse(validationService.deleteData("nonexistent"));
    }
}