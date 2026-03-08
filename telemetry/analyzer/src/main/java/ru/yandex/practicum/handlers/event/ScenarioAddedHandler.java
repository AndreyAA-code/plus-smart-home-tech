package ru.yandex.practicum.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String scenarioName = addedEvent.getName();
        
        log.info("Добавляем сценарий '{}' в хаб с hub_id = {}", scenarioName, hubId);

        // Проверяем, существует ли уже такой сценарий
        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
        
        if (existingScenario.isPresent()) {
            log.info("Сценарий '{}' уже существует для хаба {}, пропускаем", scenarioName, hubId);
            return;
        }

        // Если не существует - создаем новый
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(scenarioName);
        
        scenarioRepository.save(scenario);
        log.info("Сценарий '{}' успешно сохранен", scenarioName);
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }
}
