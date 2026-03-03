package ru.yandex.practicum.telemetry.collector.service.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.sensor_event.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor_event.SensorEventType;

public interface SensorEventHandler {

    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
