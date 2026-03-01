package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import ru.yandex.practicum.grpc.telemetry.collector.CollectorResponse;
import ru.yandex.practicum.grpc.telemetry.event.*;

import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;
import java.util.Random;

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

    private void sendEvent(SensorEventProto event) {
      log.info("Отправляю данные: {}", event.getAllFields());
      CollectorResponse response = collectorStub.collectSensorEvent(event);
      log.info("Получил ответ от коллектора: {}", response);
   }

   private SensorEventProto createTemperatureSensorEvent(SensorConfig.TemperatureSensor sensor) {
      int temperatureCelsius = getRandomSensorValue(sensor.getTemperature());
      int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);
      Instant ts = Instant.now();


      return SensorEventProto.newBuilder()
              .setId(sensor.getId())
              .setTimestamp(Timestamp.newBuilder()
                      .setSeconds(ts.getEpochSecond())
                      .setNanos(ts.getNano())
                      .build()
              ).setTemperatureSensor(
                      TemperatureSensorProto.newBuilder()
                              .setTemperatureC(temperatureCelsius)
                              .setTemperatureF(temperatureFahrenheit)
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