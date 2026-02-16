package ru.practicum.model.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.hub_event.HubEvent;
import ru.practicum.model.hub_event.HubEventType;
import ru.practicum.model.sensor_event.SensorEvent;
import ru.practicum.model.sensor_event.SensorEventType;

@RestController
@Slf4j
@RequestMapping("/events")
public class EventsController {

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@Valid @RequestBody SensorEvent request) {
        log.info("json : {}", request.toString());

        if (request.getType() == SensorEventType.LIGHT_SENSOR_EVENT) {

        } else if (request.getType() == SensorEventType.CLIMATE_SENSOR_EVENT) {

        } else if (request.getType() == SensorEventType.MOTION_SENSOR_EVENT) {

        } else if (request.getType() == SensorEventType.SWITCH_SENSOR_EVENT) {

        } else if (request.getType() == SensorEventType.TEMPERATURE_SENSOR_EVENT) {

        } else {
            throw new IllegalArgumentException("Не могу найти обработчик для типа: " + request.getType());
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
        log.info("json : {}", request.toString());
        if (request.getType() == HubEventType.DEVICE_ADDED) {

        } else if (request.getType() == HubEventType.DEVICE_REMOVED) {

        } else if (request.getType() == HubEventType.SCENARIO_ADDED) {

        } else if (request.getType() == HubEventType.SCENARIO_REMOVED) {

        } else {
            throw new IllegalArgumentException("Не могу найти обработчик для типа: \" + request.getType()");
        }
    }
}
