package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrdersDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto create(DeliveryDto deliveryDto);

    void successful(UUID orderId);

    void picked(UUID orderId);

    void failed(UUID orderId);

    BigDecimal cost(OrdersDto orderDto);
}
