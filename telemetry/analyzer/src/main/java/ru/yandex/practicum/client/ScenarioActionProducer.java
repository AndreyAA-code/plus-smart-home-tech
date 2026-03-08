package ru.yandex.practicum.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.model.Action;

import java.time.Instant;

@Slf4j
@Service
public class ScenarioActionProducer {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    public ScenarioActionProducer(
            @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub) {
        this.hubRouterStub = hubRouterStub;
        log.info("ScenarioActionProducer инициализирован");
    }

    public void sendAction(Action action) {
        log.info("Отправка действия: scenario={}, sensor={}, type={}, value={}",
                action.getScenario().getName(),
                action.getSensor().getId(),
                action.getType(),
                action.getValue());

        try {
            DeviceActionRequest request = mapToActionRequest(action);
            Empty response = hubRouterStub.handleDeviceAction(request);
            log.info("Действие успешно отправлено в hub-router");
        } catch (Exception e) {
            log.error("Ошибка отправки действия в hub-router: {}", e.getMessage());
        }
    }

    private DeviceActionRequest mapToActionRequest(Action action) {
        return DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(mapActionType(action.getType()))
                        .setValue(action.getValue() != null ? action.getValue() : 0)
                        .build())
                .setTimestamp(getCurrentTimestamp())
                .build();
    }

    private ActionTypeProto mapActionType(ActionTypeAvro actionType) {
        if (actionType == null) return ActionTypeProto.ACTIVATE;
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }

    private Timestamp getCurrentTimestamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
