package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingStoreFeignClient;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.dto.product.ProductDto;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreFeignClient {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Get products for category {}", category);
        Page<ProductDto> response = shoppingStoreService.getProducts(category, pageable);
        log.info("Show products", response);
        return response;
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
       log.info("Create new product {}", productDto);
        ProductDto response = shoppingStoreService.addProduct(productDto);
        log.info("Show products", response);
        return response;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Update product {}", productDto);
        ProductDto response = shoppingStoreService.updateProduct(productDto);
        log.info("Show products", response);
        return response;
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        log.info("Remove product {}", productId);
        boolean response = shoppingStoreService.removeProduct(productId);
        log.info("Show products", response);
        return response;
    }

    @Override
    public boolean setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState) {

       log.info("Set product quantity state {}", quantityState);

        boolean response = shoppingStoreService.updateQuantityState(productId, quantityState);
        log.info("Show products", response);
        return response;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("Get product {}", productId);
        ProductDto response = shoppingStoreService.getProductById(productId);
        log.info("Show products", response);
        return response;
    }
}