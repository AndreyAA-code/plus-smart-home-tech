package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;

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
      collectorStub.collectSensorEvent(event);
      log.info("Получил Empty ответ от коллектора");
   }

   private SensorEventProto createMotionSensorEvent(SensorConfig.MotionSensor sensor) {
       int linkQuality = getRandomSensorValue(sensor.getLinkQuality());
       int voltage = getRandomSensorValue(sensor.getVoltage());
        Instant ts = Instant.now();
        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
               .setTimestamp(Timestamp.newBuilder()
                       .setSeconds(ts.getEpochSecond())
                       .setNanos(ts.getNano())
                       .build()
               ).setMotionSensor(
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

    public SensorEventProto createLightSensorEvent(SensorConfig.LightSensor sensor) {
        int luminosity = getRandomSensorValue(sensor.getLuminosity());
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setLightSensor(
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

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setClimateSensor(
                        ClimateSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setHumidity(humidity)
                                .setCo2Level(co2Level)
                                .build()
                )
                .build();
    }

    public SensorEventProto createSwitchSensorEvent(SensorConfig.SwitchSensor sensor) {
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setSwitchSensor(
                        SwitchSensorProto.newBuilder()
                                .setState(random.nextBoolean()) // Случайное состояние вкл/выкл
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