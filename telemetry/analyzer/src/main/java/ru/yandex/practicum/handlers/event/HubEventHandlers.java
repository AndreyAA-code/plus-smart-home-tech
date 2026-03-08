package ru.yandex.practicum.handlers.event;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HubEventHandlers {
    private final Map<String, HubEventHandler> handlers = new HashMap<>();

    public HubEventHandlers(List<HubEventHandler> handlerList) {
        for (HubEventHandler handler : handlerList) {
            // Определяем тип события по классу обработчика
            String eventType = extractEventType(handler);
            handlers.put(eventType, handler);
        }
    }

    private String extractEventType(HubEventHandler handler) {
        // Возвращает имя класса события, которое обрабатывает этот handler
        // Например, для DeviceAddedEventHandler вернет "DeviceAddedEventProto"
        String handlerName = handler.getClass().getSimpleName();
        if (handlerName.contains("DeviceAdded")) {
            return "DeviceAddedEventProto";
        } else if (handlerName.contains("DeviceRemoved")) {
            return "DeviceRemovedEventProto";
        } else if (handlerName.contains("ScenarioAdded")) {
            return "ScenarioAddedEventProto";
        } else if (handlerName.contains("ScenarioRemoved")) {
            return "ScenarioRemovedEventProto";
        }
        return handlerName.replace("Handler", "");
    }

    public Map<String, HubEventHandler> getHandlers() {
        return handlers;
    }
}
