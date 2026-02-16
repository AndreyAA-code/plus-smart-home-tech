package ru.practicum.model.hub_event.device;


import ru.practicum.model.hub_event.DeviceType;
import ru.practicum.model.hub_event.HubEvent;
import ru.practicum.model.hub_event.HubEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank
    private String id;

    @NotNull
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}


