package ru.yandex.practicum.handlers.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.ScenarioActionProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotHandler {
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionProducer scenarioActionProducer;

    public void handle(SensorsSnapshotAvro sensorsSnapshot) {
        log.info("Обработка снапшота для хаба: {}", sensorsSnapshot.getHubId());
        Map<String, SensorStateAvro> sensorStateMap = sensorsSnapshot.getSensorsState();
        
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshot.getHubId());
        log.info("Найдено {} сценариев для хаба {}", scenarios.size(), sensorsSnapshot.getHubId());
        
        scenarios.stream()
                .filter(scenario -> handleScenario(scenario, sensorStateMap))
                .forEach(scenario -> {
                    log.info("Сценарий '{}' выполнен, отправляем действия", scenario.getName());
                    sendScenarioActions(scenario);
                });
    }

    private Boolean handleScenario(Scenario scenario, Map<String, SensorStateAvro> sensorStateMap) {
        List<Condition> conditions = conditionRepository.findAllByScenario(scenario);
        log.debug("Сценарий '{}' содержит {} условий", scenario.getName(), conditions.size());

        return conditions.stream().noneMatch(condition -> !checkCondition(condition, sensorStateMap));
    }

    private Boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorStateMap) {
        String sensorId = condition.getSensor().getId();
        SensorStateAvro sensorState = sensorStateMap.get(sensorId);
        
        if (sensorState == null) {
            log.debug("Датчик {} не найден в снапшоте", sensorId);
            return false;
        }

        log.debug("Проверка условия для датчика {}, тип {}", sensorId, condition.getType());
        
        switch (condition.getType()) {
            case LUMINOSITY -> {
                LightSensorAvro lightSensor = (LightSensorAvro) sensorState.getData();
                return handleOperation(condition, lightSensor.getLuminosity());
            }
            case TEMPERATURE -> {
                ClimateSensorAvro temperatureSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, temperatureSensor.getTemperatureC());
            }
            case MOTION -> {
                MotionSensorAvro motionSensor = (MotionSensorAvro) sensorState.getData();
                return handleOperation(condition, motionSensor.getMotion() ? 1 : 0);
            }
            case SWITCH -> {
                SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorState.getData();
                return handleOperation(condition, switchSensor.getState() ? 1 : 0);
            }
            case CO2LEVEL -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getCo2Level());
            }
            case HUMIDITY -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getHumidity());
            }
            default -> {
                log.warn("Неизвестный тип условия: {}", condition.getType());
                return false;
            }
        }
    }

    private Boolean handleOperation(Condition condition, Integer currentValue) {
        ConditionOperationAvro operation = condition.getOperation();
        Integer targetValue = condition.getValue();
        
        log.debug("Сравнение: текущее={}, операция={}, целевое={}", 
                currentValue, operation, targetValue);
        
        return switch (operation) {
            case EQUALS -> targetValue.equals(currentValue);
            case LOWER_THAN -> currentValue < targetValue;
            case GREATER_THAN -> currentValue > targetValue;
        };
    }

    private void sendScenarioActions(Scenario scenario) {
        log.info("Отправка действий для сценария '{}'", scenario.getName());
        actionRepository.findAllByScenario(scenario).forEach(scenarioActionProducer::sendAction);
    }
}
