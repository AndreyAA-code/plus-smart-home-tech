package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.DeliveryFeignClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryFeignClient {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        log.info("Create delivery: {}", deliveryDto);
        return deliveryService.create(deliveryDto);
    }

    @Override
    public void successful(UUID orderId) {
        log.info("Successful delivery: {}", orderId);
        deliveryService.successful(orderId);
    }

    @Override
    public void picked(UUID orderId) {
        log.info("Picked delivery: {}", orderId);
        deliveryService.picked(orderId);
    }

    @Override
    public void failed(UUID orderId) {
        log.info("Failed delivery: {}", orderId);
        deliveryService.failed(orderId);
    }

    @Override
    public BigDecimal cost(OrderDto orderDto) {
        log.info("Cost of delivery: {}", orderDto);
        return deliveryService.cost(orderDto);
    }
}
