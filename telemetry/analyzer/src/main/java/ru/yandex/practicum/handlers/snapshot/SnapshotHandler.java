package ru.yandex.practicum.handlers.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.client.ScenarioActionProducer;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioActionProducer actionProducer;

    @Transactional
    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Обработка снапшота для хаба: {}, timestamp: {}", hubId, snapshot.getTimestamp());

        // Получаем все сценарии для этого хаба
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        log.info("Найдено {} сценариев для хаба {}", scenarios.size(), hubId);

        // Для каждого сценария проверяем условия
        for (Scenario scenario : scenarios) {
            log.info("Проверка сценария: {}", scenario.getName());
            // TODO: реализовать проверку условий и выполнение действий
            // TODO: для каждого действия вызывать actionProducer.sendAction(action)
        }
    }
}
