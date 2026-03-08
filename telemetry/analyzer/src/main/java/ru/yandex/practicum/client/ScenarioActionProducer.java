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
        log.info("ScenarioActionProducer инициализирован с адресом: localhost:59090");
    }

    public void sendAction(Action action, String hubId, String scenarioName, String sensorId) {
        log.info("Отправляем действие в hub-router: hubId={}, scenario={}, sensor={}, type={}, value={}", 
                hubId, scenarioName, sensorId, action.getType(), action.getValue());

        try {
            DeviceActionRequest request = mapToActionRequest(action, hubId, scenarioName, sensorId);
            log.info("Сформирован запрос: {}", request);
            
            Empty response = hubRouterStub.handleDeviceAction(request);
            log.info("Действие успешно отправлено в hub-router, ответ получен");
        } catch (Exception e) {
            log.error("Ошибка отправки действия в hub-router: {}", e.getMessage(), e);
        }
    }

    private DeviceActionRequest mapToActionRequest(Action action, String hubId, String scenarioName, String sensorId) {
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(sensorId)
                        .setType(mapActionType(action.getType()))
                        .setValue(action.getValue() != null ? action.getValue() : 0)
                        .build())
                .setTimestamp(getCurrentTimestamp())
                .build();
    }

    private ActionTypeProto mapActionType(ActionTypeAvro actionType) {
        log.info("Маппинг типа действия из Avro {} в Proto", actionType);
        if (actionType == null) {
            return ActionTypeProto.ACTIVATE;
        }
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
