package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

@PostMapping
PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

}
