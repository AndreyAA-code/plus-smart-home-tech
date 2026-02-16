package ru.practicum.model.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.hub_event.HubEvent;
import ru.practicum.model.hub_event.HubEventType;
import ru.practicum.model.sensor_event.SensorEvent;
import ru.practicum.model.sensor_event.SensorEventType;
import ru.practicum.model.service.HubEventHandler;
import ru.practicum.model.service.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/events")
public class EventController {

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(List<SensorEventHandler> sensorEventHandlerList, List<HubEventHandler> hubEventHandlerList) {
        this.sensorEventHandlers = sensorEventHandlerList.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlerList.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@Valid @RequestBody SensorEvent request) {
        SensorEventHandler sensorEventHandler = sensorEventHandlers.get(request.getType());
        if (sensorEventHandler == null) {
            throw new IllegalArgumentException("Не могу найти обработчик для типа: " + request.getType());
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
       HubEventHandler hubEventHandler = hubEventHandlers.get(request.getType());
        if (hubEventHandler == null) {
            throw new IllegalArgumentException("Не могу найти обработчик для типа: " + request.getType());
        }
    }
}
