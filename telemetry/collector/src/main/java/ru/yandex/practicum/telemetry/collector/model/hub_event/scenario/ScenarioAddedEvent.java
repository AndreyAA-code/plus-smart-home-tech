package ru.yandex.practicum.telemetry.collector.model.hub_event.scenario;


import jakarta.validation.constraints.Size;
import ru.yandex.practicum.telemetry.collector.model.hub_event.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub_event.HubEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    private String name;

    @NotNull
    List<ScenarioCondition> conditions;

    @NotNull
    List<DeviceAction> actions;


    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}

