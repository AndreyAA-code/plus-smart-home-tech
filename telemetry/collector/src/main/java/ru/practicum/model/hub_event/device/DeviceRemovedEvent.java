package ru.practicum.model.hub_event.device;


import ru.practicum.model.hub_event.HubEvent;
import ru.practicum.model.hub_event.HubEventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {

    @NotBlank
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}

