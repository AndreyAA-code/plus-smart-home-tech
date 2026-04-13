package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrdersDto orderDto);

    BigDecimal getTotalCost(OrdersDto orderDto);

    void paymentRefunded(UUID paymentId);

    BigDecimal productCost(OrdersDto orderDto);

    void paymentFailed(UUID paymentId);
}
