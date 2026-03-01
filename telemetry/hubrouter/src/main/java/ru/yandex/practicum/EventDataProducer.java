package ru.yandex.practicum;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;

@Component
public class EventDataProducer {

   @GrpcClient("collector")
   private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

}