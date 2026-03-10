package ru.yandex.practicum.handlers.event;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventHandlers {
    private final List<HubEventHandler> handlerList;
    private final Map<String, HubEventHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Регистрация обработчиков событий хаба...");
        for (HubEventHandler handler : handlerList) {
            String payloadType = handler.getPayloadType();
            log.info("Зарегистрирован обработчик для типа: {}", payloadType);
            handlerMap.put(payloadType, handler);
        }
        log.info("Всего зарегистрировано обработчиков: {}", handlerMap.size());
    }

    public Map<String, HubEventHandler> getHandlers() {
        return handlerMap;
    }
}
