package ru.yandex.practicum.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String scenarioName = addedEvent.getName();
        
        log.info("Добавляем сценарий '{}' в хаб с hub_id = {}", scenarioName, hubId);

        // Проверяем, существует ли уже такой сценарий
        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
        Scenario scenario;
        
        if (existingScenario.isPresent()) {
            log.info("Сценарий '{}' уже существует для хаба {}, обновляем", scenarioName, hubId);
            scenario = existingScenario.get();
        } else {
            // Если не существует - создаем новый
            scenario = Scenario.builder()
                    .hubId(hubId)
                    .name(scenarioName)
                    .build();
            scenario = scenarioRepository.save(scenario);
            log.info("Сценарий '{}' успешно создан", scenarioName);
        }

        // Сохраняем условия
        List<ScenarioConditionAvro> conditions = addedEvent.getConditions();
        if (conditions != null && !conditions.isEmpty()) {
            log.info("Сохраняем {} условий для сценария '{}'", conditions.size(), scenarioName);
            
            for (ScenarioConditionAvro conditionAvro : conditions) {
                // Преобразуем значение в Integer
                Integer value = extractIntegerValue(conditionAvro.getValue());
                
                // Создаем условие
                Condition condition = Condition.builder()
                        .type(conditionAvro.getType())
                        .operation(conditionAvro.getOperation())
                        .value(value)
                        .build();
                condition = conditionRepository.save(condition);
                
                // Находим сенсор (он должен существовать)
                Sensor sensor = sensorRepository.findById(conditionAvro.getSensorId())
                        .orElseThrow(() -> new RuntimeException("Sensor not found: " + conditionAvro.getSensorId()));
                
                // Создаем связь
                ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                        .scenario(scenario)
                        .sensor(sensor)
                        .condition(condition)
                        .build();
                scenarioConditionRepository.save(scenarioCondition);
                log.info("Условие для сенсора {} сохранено", conditionAvro.getSensorId());
            }
        }

        // Сохраняем действия
        List<DeviceActionAvro> actions = addedEvent.getActions();
        if (actions != null && !actions.isEmpty()) {
            log.info("Сохраняем {} действий для сценария '{}'", actions.size(), scenarioName);
            
            for (DeviceActionAvro actionAvro : actions) {
                // Находим сенсор
                Sensor sensor = sensorRepository.findById(actionAvro.getSensorId())
                        .orElseThrow(() -> new RuntimeException("Sensor not found: " + actionAvro.getSensorId()));
                
                // Создаем действие
                Action action = Action.builder()
                        .type(actionAvro.getType())
                        .value(actionAvro.getValue() != null ? actionAvro.getValue() : 0)
                        .scenario(scenario)
                        .sensor(sensor)
                        .build();
                
                actionRepository.save(action);
                log.info("Действие для сенсора {} сохранено", actionAvro.getSensorId());
            }
        }
        
        log.info("Сценарий '{}' полностью обработан", scenarioName);
    }

    private Integer extractIntegerValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }
}
