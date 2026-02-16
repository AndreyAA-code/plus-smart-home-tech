package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.telemetry.collector.model.hub_event.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub_event.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();
    void handle(HubEvent event);
}
