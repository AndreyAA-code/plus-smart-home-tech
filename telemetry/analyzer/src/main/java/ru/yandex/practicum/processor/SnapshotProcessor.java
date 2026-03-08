package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.ScenarioAnalyzer;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final ScenarioAnalyzer scenarioAnalyzer;
    
    @Value("${kafka.topics.snapshots}")
    private String snapshotsTopic;

    public void start() {
        try {
            snapshotConsumer.subscribe(List.of(snapshotsTopic));
            log.info("Подписались на топик снапшотов: {}", snapshotsTopic);

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    log.info("Получили снапшот от хаба: {}, timestamp: {}", 
                            snapshot.getHubId(), snapshot.getTimestamp());

                    scenarioAnalyzer.analyze(snapshot);
                }

                snapshotConsumer.commitSync();
                log.debug("Коммит смещений для топика снапшотов");
            }
        } catch (WakeupException ignored) {
            log.info("Получен сигнал завершения для SnapshotProcessor");
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", snapshotsTopic, e);
        } finally {
            try {
                snapshotConsumer.commitSync();
            } finally {
                snapshotConsumer.close();
                log.info("SnapshotProcessor остановлен");
            }
        }
    }
}
