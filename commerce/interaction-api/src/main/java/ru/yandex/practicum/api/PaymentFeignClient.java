package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

@PostMapping
PaymentDto createPayment(@RequestBody @Valid OrdersDto orderDto);

@PostMapping ("/totalCost")
BigDecimal getTotalCost(@RequestBody @Valid OrdersDto orderDto);

@PostMapping("/refund")
void paymentRefunded(@RequestBody UUID paymentId);

@PostMapping ("/productCost")
BigDecimal getProductCost(@RequestBody @Valid OrdersDto orderDto);

@PostMapping("/failed")
void paymentFailed(@RequestBody UUID paymentId);

}
