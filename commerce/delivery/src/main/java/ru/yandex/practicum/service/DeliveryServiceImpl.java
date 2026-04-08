package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;


    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        return null;
    }

    @Override
    public void successful(UUID orderId) {

    }

    @Override
    public void picked(UUID orderId) {

    }

    @Override
    public void failed(UUID orderId) {

    }

    @Override
    public BigDecimal cost(OrderDto orderDto) {
        return null;
    }
}
