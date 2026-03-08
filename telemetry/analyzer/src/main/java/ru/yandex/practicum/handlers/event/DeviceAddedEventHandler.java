package ru.yandex.practicum.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) event.getPayload();
        log.info("Обработка DeviceAddedEvent: hubId={}, deviceId={}, deviceType={}",
                event.getHubId(), deviceAddedEvent.getId(), deviceAddedEvent.getType());

        // TODO: сохранить датчик в БД
    }
}
