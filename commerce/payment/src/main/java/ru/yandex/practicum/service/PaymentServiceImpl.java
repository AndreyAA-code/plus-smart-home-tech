package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto createPayment(OrdersDto orderDto) {
        log.info("Create payment {}", orderDto);

        return null;
    }

    @Override
    public BigDecimal getTotalCost(OrdersDto orderDto) {
        return null;
    }

    @Override
    public void paymentRefunded(UUID paymentId) {

    }

    @Override
    public BigDecimal productCost(OrdersDto orderDto) {

        return null;
    }

    @Override
    public void paymentFailed(UUID paymentId) {

    }
}
