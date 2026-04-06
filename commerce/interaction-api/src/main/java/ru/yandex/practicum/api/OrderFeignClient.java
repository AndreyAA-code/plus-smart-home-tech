package ru.yandex.practicum.api;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderFeignClient {

    @GetMapping
    List<OrderDto> getOrders(@RequestParam(name = "username") @NotNull String username);

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest orderRequest);

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest returnRequest);

    @PostMapping("/payment")
    OrderDto payment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto complete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody @NotNull UUID orderId);

}