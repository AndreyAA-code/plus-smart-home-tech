package ru.yandex.practicum.telemetry.collector.config;

import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.telemetry.collector.interceptor.LoggingInterceptor;

@Configuration
public class GrpcConfig {
    
    @GrpcGlobalServerInterceptor
    LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }
}