package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class EventDataProducer {

    private static final Logger log = LoggerFactory.getLogger(EventDataProducer.class);
    private final Random random = new Random();
    private final SensorConfig sensorConfig;

    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    public EventDataProducer(SensorConfig sensorConfig) {
        this.sensorConfig = sensorConfig;
    }

    // Генерация и отправка события от случайного датчика каждую секунду
    @Scheduled(fixedRate = 1000)
    public void generateAndSendRandomEvent() {
        // Случайно выбираем тип датчика (0 - Climate, 1 - Light, 2 - Motion, 3 - Switch, 4 - Temperature)
        int sensorType = ThreadLocalRandom.current().nextInt(5);

        switch (sensorType) {
            case 0 -> {
                SensorConfig.ClimateSensor sensor = getRandomSensor(sensorConfig.getClimateSensors());
                log.debug("Создаем событие от климатического датчика на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createClimateSensorEvent(sensor));
                }
            }
            case 1 -> {
                SensorConfig.LightSensor sensor = getRandomSensor(sensorConfig.getLightSensors());
                log.debug("Создаем событие от датчика освещенности на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createLightSensorEvent(sensor));
                }
            }
            case 2 -> {
                SensorConfig.MotionSensor sensor = getRandomSensor(sensorConfig.getMotionSensors());
                log.debug("Создаем событие от датчика движения на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createMotionSensorEvent(sensor));
                }
            }
            case 3 -> {
                SensorConfig.SwitchSensor sensor = getRandomSensor(sensorConfig.getSwitchSensors());
                log.debug("Создаем событие от датчика выключателя на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createSwitchSensorEvent(sensor));
                }
            }
            case 4 -> {
                SensorConfig.TemperatureSensor sensor = getRandomSensor(sensorConfig.getTemperatureSensors());
                log.debug("Создаем событие от датчика температуры на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createTemperatureSensorEvent(sensor));
                }
            }
            default -> throw new IllegalStateException("Сгенерировали датчик неизвестного типа: " + sensorType);
        }
    }

    // Выбор случайного датчика из списка датчиков одного типа
    private <T> T getRandomSensor(List<T> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return null;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(sensors.size());
        return sensors.get(randomIndex);
    }

    private void sendEvent(SensorEventProto event) {
        log.info("=== ОТПРАВКА СОБЫТИЯ ===");
        log.info("Тип события: {}", event.getPayloadCase());
        log.info("ID: {}", event.getId());
        log.info("HubId: {}", event.getHubId());
        log.info("Timestamp: {}", event.getTimestamp());
        log.info("Все поля: {}", event.getAllFields());

        // Проверяем валидность сообщения перед отправкой
        try {
            byte[] bytes = event.toByteArray();
            log.info("Размер сообщения: {} байт", bytes.length);

            // Пробуем распарсить обратно
            SensorEventProto parsed = SensorEventProto.parseFrom(bytes);
            log.info("✓ Сообщение валидно, обратное парсинг успешен");

            // Проверяем, что данные не потерялись
            if (!parsed.getId().equals(event.getId())) {
                log.error("✗ ID не совпадает после парсинга!");
            }
            if (parsed.getPayloadCase() != event.getPayloadCase()) {
                log.error("✗ PayloadCase не совпадает после парсинга!");
            }

        } catch (Exception e) {
            log.error("✗ Сообщение НЕВАЛИДНО: {}", e.getMessage(), e);
            // Логируем сырые байты для диагностики
            try {
                byte[] bytes = event.toByteArray();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(bytes.length, 100); i++) {
                    sb.append(String.format("%02X ", bytes[i]));
                }
                log.error("Первые 100 байт: {}", sb.toString());
            } catch (Exception ex) {
                log.error("Не удалось получить байты");
            }
            return; // Не отправляем невалидное сообщение
        }

        log.info("Отправка в gRPC...");
        try {
            collectorStub.collectSensorEvent(event);
            log.info("✓ Получил Empty ответ от коллектора");
        } catch (Exception e) {
            log.error("✗ Ошибка при отправке: {}", e.getMessage(), e);
        }
    }

    private SensorEventProto createMotionSensorEvent(SensorConfig.MotionSensor sensor) {
        int linkQuality = getRandomSensorValue(sensor.getLinkQuality());
        int voltage = getRandomSensorValue(sensor.getVoltage());
        Instant ts = Instant.now();

        log.debug("Создание MotionSensorEvent: id={}, linkQuality={}, voltage={}",
                sensor.getId(), linkQuality, voltage);

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build()
                ).setMotionSensorEvent(
                        MotionSensorProto.newBuilder()
                                .setLinkQuality(linkQuality)
                                .setVoltage(voltage)
                                .build()
                )
                .build();
    }

    private SensorEventProto createTemperatureSensorEvent(SensorConfig.TemperatureSensor sensor) {
        int temperatureCelsius = getRandomSensorValue(sensor.getTemperature());
        int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);
        Instant ts = Instant.now();

        log.debug("Создание TemperatureSensorEvent: id={}, temperatureC={}",
                sensor.getId(), temperatureCelsius);

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build()
                ).setTemperatureSensorEvent(
                        TemperatureSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setTemperatureF(temperatureFahrenheit)
                                .build()
                )
                .build();
    }

    public SensorEventProto createLightSensorEvent(SensorConfig.LightSensor sensor) {
        int luminosity = getRandomSensorValue(sensor.getLuminosity());
        Instant ts = Instant.now();

        log.debug("Создание LightSensorEvent: id={}, luminosity={}",
                sensor.getId(), luminosity);

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setLightSensorEvent(
                        LightSensorProto.newBuilder()
                                .setLinkQuality(100)
                                .setLuminosity(luminosity)
                                .build()
                )
                .build();
    }

    public SensorEventProto createClimateSensorEvent(SensorConfig.ClimateSensor sensor) {
        int temperatureCelsius = getRandomSensorValue(sensor.getTemperature());
        int humidity = getRandomSensorValue(sensor.getHumidity());
        int co2Level = getRandomSensorValue(sensor.getCo2Level());
        Instant ts = Instant.now();

        log.debug("Создание ClimateSensorEvent: id={}, temp={}, humidity={}, co2={}",
                sensor.getId(), temperatureCelsius, humidity, co2Level);

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setClimateSensorEvent(
                        ClimateSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setHumidity(humidity)
                                .setCo2Level(co2Level)
                                .build()
                )
                .build();
    }

    public SensorEventProto createSwitchSensorEvent(SensorConfig.SwitchSensor sensor) {
        boolean state = random.nextBoolean();
        Instant ts = Instant.now();

        log.debug("Создание SwitchSensorEvent: id={}, state={}",
                sensor.getId(), state);

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setSwitchSensorEvent(
                        SwitchSensorProto.newBuilder()
                                .setState(state)
                                .build()
                )
                .build();
    }

    private int getRandomSensorValue(SensorConfig.Range range) {
        if (range == null) return 0;
        return range.getMinValue() +
                random.nextInt(range.getMaxValue() - range.getMinValue() + 1);
    }
}