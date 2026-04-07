package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.OrdersFeignClient;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.repository.OrdersRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrdersServiceImpl implements OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrdersFeignClient ordersClient;

    @Override
    public List<OrderDto> getOrders(String username) {
        return List.of();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest orderRequest) {
        return null;
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        return null;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto complete(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return null;
    }
}
