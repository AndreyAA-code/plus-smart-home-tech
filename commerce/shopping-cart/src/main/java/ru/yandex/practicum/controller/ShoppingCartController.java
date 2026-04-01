package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingCartFeignClient;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.product.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartFeignClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Get shopping cart for user {}", username);
        ShoppingCartDto response = shoppingCartService.getShoppingCart(username);
        log.info("Show shopping cart", response);
        return response;
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        log.info("Add product to cart for user {}", username);
        ShoppingCartDto response = shoppingCartService.addProductToShoppingCart(username, products);
        log.info("Show shopping Cart {}", response);
        return response;
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        log.info("Deactivate current shopping cart for user {}", username);
        shoppingCartService.deactivateCurrentShoppingCart(username);
        log.info("Shopping cart for user {} is deactivated", username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {
        log.info("Remove product from cart for user {}", username);
        ShoppingCartDto response = shoppingCartService.removeFromShoppingCart(username, products);
        log.info("Show shopping Cart {}", response);
        return response;
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
       log.info("Change product quantity for cart for user {}", username);
        ShoppingCartDto response = shoppingCartService.changeProductQuantity(username, request);
        log.info("Show shopping Cart {}", response);
        return response;
    }
}