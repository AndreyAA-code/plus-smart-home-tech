package ru.yandex.practicum.telemetry.collector.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Slf4j
@Component
public class LoggingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();
        log.info("=== gRPC вызов ===");
        log.info("Метод: {}", methodName);
        log.info("Заголовки: {}", headers);

        // Логируем входящие данные
        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(RespT message) {
                log.info("Отправка ответа");
                super.sendMessage(message);
            }
        };

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(wrappedCall, headers)) {

            @Override
            public void onMessage(ReqT message) {
                log.info("Получено сообщение типа: {}", message.getClass().getName());
                if (message instanceof com.google.protobuf.Message) {
                    com.google.protobuf.Message protoMsg = (com.google.protobuf.Message) message;
                    log.info("Сообщение: {}", protoMsg);
                    log.info("Все поля: {}", protoMsg.getAllFields());
                }
                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                log.info("Получен onHalfClose");
                super.onHalfClose();
            }

            @Override
            public void onCancel() {
                log.warn("Вызов отменен");
                super.onCancel();
            }

            @Override
            public void onComplete() {
                log.info("Вызов завершен");
                super.onComplete();
            }
        };
    }
}