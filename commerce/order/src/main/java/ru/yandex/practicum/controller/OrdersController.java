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
        log.info("Get orders for user {}", username);
        return ordersService.getOrders(username);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest orderRequest) {
        log.info("Create new order {}", orderRequest);
        return ordersService.createNewOrder(orderRequest);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        log.info("Return order {}", returnRequest);
        return ordersService.returnOrder(returnRequest);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Payment order {}", orderId);
        return ordersService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Payment failed order {}", orderId);
        return ordersService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Delivery order {}", orderId);
        return ordersService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Delivery failed order {}", orderId);
        return ordersService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Complete order {}", orderId);
        return ordersService.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Calculate total cost for order {}", orderId);
        return ordersService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Calculate delivery cost for order {}", orderId);
        return ordersService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Assemble order {}", orderId);
        return ordersService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Assemble failed order {}", orderId);
        return ordersService.assemblyFailed(orderId);
    }
}
