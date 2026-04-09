package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrdersService {

    List<OrdersDto> getOrders(String username);

    OrdersDto createNewOrder(CreateNewOrderRequest orderRequest);

    OrdersDto returnOrder(ProductReturnRequest returnRequest);

    OrdersDto payment(UUID orderId);

    OrdersDto paymentFailed(UUID orderId);

    OrdersDto delivery(UUID orderId);

    OrdersDto deliveryFailed(UUID orderId);

    OrdersDto complete(UUID orderId);

    OrdersDto calculateTotalCost(UUID orderId);

    OrdersDto calculateDeliveryCost(UUID orderId);

    OrdersDto assembly(UUID orderId);

    OrdersDto assemblyFailed(UUID orderId);
}
