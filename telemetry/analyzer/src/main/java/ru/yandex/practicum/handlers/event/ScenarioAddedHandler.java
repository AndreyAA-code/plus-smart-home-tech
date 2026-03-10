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

    private static final String INSERT_SCENARIO_SQL =
            "INSERT INTO scenarios (hub_id, name) VALUES (?, ?) ON CONFLICT (hub_id, name) DO NOTHING";
    private static final String SELECT_SCENARIO_ID_SQL =
            "SELECT id FROM scenarios WHERE hub_id = ? AND name = ?";
    private static final String INSERT_ACTION_SQL =
            "INSERT INTO actions (type, value) VALUES (?, ?) RETURNING id";
    private static final String INSERT_SCENARIO_ACTION_SQL =
            "INSERT INTO scenario_actions (scenario_id, sensor_id, action_id) VALUES (?, ?, ?)";
    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String scenarioName = addedEvent.getName();
        
        log.info("Добавляем сценарий '{}' в хаб с hub_id = {}", scenarioName, hubId);

        // Сохраняем сценарий
        jdbcTemplate.update(INSERT_SCENARIO_SQL, hubId, scenarioName);
        
        // Получаем id сценария
        Long scenarioId = jdbcTemplate.queryForObject(
                SELECT_SCENARIO_ID_SQL,
            Long.class, hubId, scenarioName);
        
        log.info("Сценарий сохранен с id: {}", scenarioId);

        // Сохраняем действия
        List<DeviceActionAvro> actions = addedEvent.getActions();
        if (actions != null && !actions.isEmpty()) {
            for (DeviceActionAvro actionAvro : actions) {
                // Сохраняем действие
                Long actionId = jdbcTemplate.queryForObject(
                        INSERT_ACTION_SQL,
                    Long.class, 
                    actionAvro.getType().toString(), 
                    actionAvro.getValue() != null ? actionAvro.getValue() : 0);
                
                // Связываем действие со сценарием и сенсором
                jdbcTemplate.update(INSERT_SCENARIO_ACTION_SQL, scenarioId, actionAvro.getSensorId(), actionId);
                
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
