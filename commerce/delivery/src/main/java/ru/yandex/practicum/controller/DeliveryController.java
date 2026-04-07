package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.DeliveryFeignClient;
import ru.yandex.practicum.service.DeliveryService;

@RestController
@Slf4j
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryFeignClient {
    private final DeliveryService deliveryService;



}
