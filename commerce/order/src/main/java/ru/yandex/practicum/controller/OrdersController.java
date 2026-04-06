package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.OrdersFeignClient;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrdersService;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController implements OrdersFeignClient {
    private final OrdersService ordersService;

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
