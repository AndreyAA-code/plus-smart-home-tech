package ru.yandex.practicum.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro removedEvent = (DeviceRemovedEventAvro) event.getPayload();
        log.info("Обработка DeviceRemovedEvent: hubId={}, deviceId={}",
                event.getHubId(), removedEvent.getId());

        // TODO: удалить датчик из БД
    }
}
