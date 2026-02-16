package ru.yandex.practicum.telemetry.collector.model.hub_event.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensor_id;
    private ConditionType type;
    private ConditionOperations condition;
    private int value;
}
