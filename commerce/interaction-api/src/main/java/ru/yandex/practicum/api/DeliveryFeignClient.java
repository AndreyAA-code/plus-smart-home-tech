package ru.yandex.practicum.api;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrdersDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {

    @PutMapping
    DeliveryDto create(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successful(@RequestBody @NotNull UUID orderId);

    @PostMapping("/picked")
    void picked(@RequestBody @NotNull UUID orderId);

    @PostMapping("/failed")
    void failed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/cost")
    BigDecimal cost(@RequestBody @NotNull OrdersDto orderDto);

}
