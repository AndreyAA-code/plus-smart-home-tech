package ru.yandex.practicum.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String scenarioName = addedEvent.getName();
        
        log.info("Добавляем сценарий '{}' в хаб с hub_id = {}", scenarioName, hubId);

        // Сохраняем сценарий
        String scenarioSql = "INSERT INTO scenarios (hub_id, name) VALUES (?, ?) ON CONFLICT (hub_id, name) DO NOTHING";
        jdbcTemplate.update(scenarioSql, hubId, scenarioName);
        
        // Получаем id сценария
        Long scenarioId = jdbcTemplate.queryForObject(
            "SELECT id FROM scenarios WHERE hub_id = ? AND name = ?", 
            Long.class, hubId, scenarioName);
        
        log.info("Сценарий сохранен с id: {}", scenarioId);

        // Сохраняем действия
        List<DeviceActionAvro> actions = addedEvent.getActions();
        if (actions != null && !actions.isEmpty()) {
            for (DeviceActionAvro actionAvro : actions) {
                // Сохраняем действие
                String actionSql = "INSERT INTO actions (type, value) VALUES (?, ?) RETURNING id";
                Long actionId = jdbcTemplate.queryForObject(
                    actionSql, 
                    Long.class, 
                    actionAvro.getType().toString(), 
                    actionAvro.getValue() != null ? actionAvro.getValue() : 0);
                
                // Связываем действие со сценарием и сенсором
                String scenarioActionSql = "INSERT INTO scenario_actions (scenario_id, sensor_id, action_id) VALUES (?, ?, ?)";
                jdbcTemplate.update(scenarioActionSql, scenarioId, actionAvro.getSensorId(), actionId);
                
                log.info("Действие для сенсора {} сохранено", actionAvro.getSensorId());
            }
        }
        
        log.info("Сценарий '{}' полностью обработан", scenarioName);
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }
}
