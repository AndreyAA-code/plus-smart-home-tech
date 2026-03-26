package ru.yandex.practicum.dto.product;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    private QuantityState quantityState;
}
