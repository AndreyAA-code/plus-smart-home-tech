package ru.yandex.practicum.api;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrdersFeignClient {

    @GetMapping
    List<OrdersDto> getOrders(@RequestParam(name = "username") @NotNull String username);

    @PutMapping
    OrdersDto createNewOrder(@RequestBody CreateNewOrderRequest orderRequest);

    @PostMapping("/return")
    OrdersDto returnOrder(@RequestBody ProductReturnRequest returnRequest);

    @PostMapping("/payment")
    OrdersDto payment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrdersDto paymentFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrdersDto delivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrdersDto deliveryFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrdersDto complete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrdersDto calculateTotalCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrdersDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrdersDto assembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrdersDto assemblyFailed(@RequestBody @NotNull UUID orderId);

}