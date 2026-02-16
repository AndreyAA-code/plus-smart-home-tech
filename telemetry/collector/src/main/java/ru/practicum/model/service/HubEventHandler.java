package ru.practicum.model.service;

import ru.practicum.model.hub_event.HubEvent;
import ru.practicum.model.hub_event.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();
    void handle(HubEvent event);
}
