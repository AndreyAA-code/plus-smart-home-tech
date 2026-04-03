package ru.yandex.practicum.dto.order;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;


public class CreateNewOrderRequest {
    private ShoppingCartDto shoppingCart;
    private AddressDto address;
}
