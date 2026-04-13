package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignClient {
    private final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("New product in warehouse request {}", request);
        warehouseService.newProductInWarehouse(request);
        log.info("product added", request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        log.info("Check product quantity enough for cart {}", cartDto);
        BookedProductsDto response = warehouseService.checkProductQuantityEnoughForShoppingCart(cartDto);
        log.info("Products reserved}", response);
        return response;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Add product to warehouse request {}", request);
        warehouseService.addProductToWarehouse(request);
        log.info("Product added", request);
    }

    @Override
    public void returnProducts(Map<UUID, Integer> returnProducts) {
        log.info("Return product in warehouse request {}", returnProducts);
        warehouseService.returnProductToWarehouse(returnProducts);
    }

    @Override
    public BookedProductsDto assemblyProducts( AssemblyProductsForOrderRequest request) {
        log.info("Assembly product in warehouse request {}", request);
        return warehouseService.assemblyProducts(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Get warehouse address");
        AddressDto response = warehouseService.getWarehouseAddress();
        log.info("Warehouse address: {}", response);
        return response;
    }
}