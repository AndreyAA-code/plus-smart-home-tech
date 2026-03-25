package ru.yandex.practicum.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

@Component
@Slf4j
public class WarehouseFallback implements WarehouseFeignClient {

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        log.warn("Warehouse unavailable, using fallback");
        return new BookedProductsDto();
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return null;
    }
}