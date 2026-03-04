package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType,
                        Function.identity()
                ));

        log.info("Зарегистрировано обработчиков: сенсоров - {}, хабов - {}",
                this.sensorEventHandlers.size(), this.hubEventHandlers.size());
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("=== collectSensorEvent ВЫЗВАН ===");
        log.info("Тип запроса: {}", request.getClass().getName());
        log.info("PayloadCase: {}", request.getPayloadCase());
        log.info("ID: {}", request.getId());
        log.info("HubId: {}", request.getHubId());
        log.info("Timestamp: {}", request.getTimestamp());

        // Пробуем получить сырые байты
        try {
            byte[] bytes = request.toByteArray();
            log.info("Размер сообщения: {} байт", bytes.length);
        } catch (Exception e) {
            log.error("Не удалось получить байты", e);
        }

        try {
            if (sensorEventHandlers.containsKey(request.getPayloadCase())) {
                sensorEventHandlers.get(request.getPayloadCase()).handle(request);
                log.info("SensorEvent успешно обработано: {}", request.getId());

                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
                log.info("Ответ отправлен");
            } else {
                log.error("Нет обработчика для типа: {}", request.getPayloadCase());
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке сенсорного события", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("=== collectHubEvent ВЫЗВАН ===");
        log.info("Тип запроса: {}", request.getClass().getName());
        log.info("PayloadCase: {}", request.getPayloadCase());
        log.info("HubId: {}", request.getHubId());
        log.info("Timestamp: {}", request.getTimestamp());

        try {
            if (hubEventHandlers.containsKey(request.getPayloadCase())) {
                hubEventHandlers.get(request.getPayloadCase()).handle(request);
                log.info("HubEvent успешно обработано: {}", request.getHubId());

                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
                log.info("Ответ отправлен");
            } else {
                log.error("Нет обработчика для типа: {}", request.getPayloadCase());
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке события хаба", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
            ));
        }
    }
}