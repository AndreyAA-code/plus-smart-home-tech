package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.PaymentFeignClient;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeignClient {
    private final PaymentService paymentService;

    @Override
    public PaymentDto createPayment(OrdersDto orderDto) {
        log.info("Create payment request");
        return paymentService.createPayment(orderDto);
    }

    @Override
    public BigDecimal getTotalCost(OrdersDto orderDto) {
        log.info("Get Total cost");
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public void paymentRefunded(UUID paymentId) {
        log.info("Payment refunded request");
        paymentService.paymentRefunded(paymentId);
    }

    @Override
    public BigDecimal productCost(OrdersDto orderDto) {
        log.info("Get Product cost");
        return paymentService.productCost(orderDto);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("Payment failed request");
        paymentService.paymentFailed(paymentId);
    }
}