package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Service
@Slf4j
public class ScenarioAnalyzer {
    public void analyze(SensorsSnapshotAvro snapshot) {
        log.info("Анализ снапшота для хаба: {}", snapshot.getHubId());
        // TODO: реализовать анализ сценариев
    }
}
