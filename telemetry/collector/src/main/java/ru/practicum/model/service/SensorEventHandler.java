package ru.practicum.model.service;

import ru.practicum.model.sensor_event.SensorEvent;
import ru.practicum.model.sensor_event.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
