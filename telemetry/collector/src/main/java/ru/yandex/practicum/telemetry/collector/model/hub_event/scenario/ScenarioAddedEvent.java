package ru.yandex.practicum.telemetry.collector.model.hub_event.scenario;


import ru.yandex.practicum.telemetry.collector.model.hub_event.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub_event.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub_event.DeviceType;
import jakarta.validation.constraints.Min;
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
    @Min(3)
    private String name;

    @NotNull
    private DeviceType deviceType;

    @NotNull
    List<ScenarioCondition> conditions;

    @NotNull
    List<DeviceAction> actions;

    @NotNull
    ActionType type;



    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}

