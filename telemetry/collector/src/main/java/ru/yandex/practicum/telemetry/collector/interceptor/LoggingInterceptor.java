package ru.yandex.practicum.telemetry.collector.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class LoggingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();
        log.info("=== НОВЫЙ gRPC ВЫЗОВ ===");
        log.info("Метод: {}", methodName);
        log.info("Заголовки: {}", headers);

        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(RespT message) {
                log.info("=== ОТПРАВКА ОТВЕТА ===");
                if (message instanceof com.google.protobuf.Message) {
                    log.info("Тип ответа: {}", message.getClass().getSimpleName());
                    log.info("Ответ: {}", message);
                } else {
                    log.info("Ответ: {}", message);
                }
                super.sendMessage(message);
            }
        };

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(wrappedCall, headers)) {

            @Override
            public void onMessage(ReqT message) {
                log.info("=== ПОЛУЧЕНО СООБЩЕНИЕ ===");
                log.info("Тип сообщения: {}", message.getClass().getName());

                if (message instanceof SensorEventProto) {
                    SensorEventProto event = (SensorEventProto) message;
                    log.info("=== SENSOR EVENT ДЕТАЛИ ===");
                    log.info("PayloadCase: {}", event.getPayloadCase());
                    log.info("ID: {}", event.getId());
                    log.info("HubId: {}", event.getHubId());
                    log.info("Timestamp: {}", event.getTimestamp());
                    log.info("Все поля: {}", event.getAllFields());

                    // Детальный разбор по типу
                    switch (event.getPayloadCase()) {
                        case MOTION_SENSOR_EVENT:
                            log.info("MOTION_SENSOR данные:");
                            log.info("  linkQuality: {}", event.getMotionSensorEvent().getLinkQuality());
                            log.info("  voltage: {}", event.getMotionSensorEvent().getVoltage());
                            log.info("  motion: {}", event.getMotionSensorEvent().getMotion());
                            break;

                        case LIGHT_SENSOR_EVENT:
                            log.info("LIGHT_SENSOR данные:");
                            log.info("  linkQuality: {}", event.getLightSensorEvent().getLinkQuality());
                            log.info("  luminosity: {}", event.getLightSensorEvent().getLuminosity());
                            break;

                        case CLIMATE_SENSOR_EVENT:
                            log.info("CLIMATE_SENSOR данные:");
                            log.info("  temperatureC: {}", event.getClimateSensorEvent().getTemperatureC());
                            log.info("  humidity: {}", event.getClimateSensorEvent().getHumidity());
                            log.info("  co2Level: {}", event.getClimateSensorEvent().getCo2Level());
                            break;

                        case TEMPERATURE_SENSOR_EVENT:
                            log.info("TEMPERATURE_SENSOR данные:");
                            log.info("  temperatureC: {}", event.getTemperatureSensorEvent().getTemperatureC());
                            log.info("  temperatureF: {}", event.getTemperatureSensorEvent().getTemperatureF());
                            break;

                        case SWITCH_SENSOR_EVENT:
                            log.info("SWITCH_SENSOR данные:");
                            log.info("  state: {}", event.getSwitchSensorEvent().getState());
                            break;

                        case PAYLOAD_NOT_SET:
                            log.warn("PAYLOAD NOT SET!");
                            break;

                        default:
                            log.warn("Неизвестный тип события: {}", event.getPayloadCase());
                    }

                } else if (message instanceof HubEventProto) {
                    HubEventProto event = (HubEventProto) message;
                    log.info("=== HUB EVENT ДЕТАЛИ ===");
                    log.info("PayloadCase: {}", event.getPayloadCase());
                    log.info("HubId: {}", event.getHubId());
                    log.info("Timestamp: {}", event.getTimestamp());
                    log.info("Все поля: {}", event.getAllFields());

                } else {
                    log.info("Сообщение другого типа: {}", message);
                }

                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                log.info("=== onHalfClose ===");
                super.onHalfClose();
            }

            @Override
            public void onCancel() {
                log.warn("=== ВЫЗОВ ОТМЕНЕН ===");
                super.onCancel();
            }

            @Override
            public void onComplete() {
                log.info("=== ВЫЗОВ ЗАВЕРШЕН ===");
                super.onComplete();
            }

            @Override
            public void onReady() {
                log.debug("onReady");
                super.onReady();
            }
        };
    }
}