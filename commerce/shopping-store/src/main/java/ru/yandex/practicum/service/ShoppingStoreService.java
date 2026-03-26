package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.QuantityState;

import java.util.UUID;

@Service
public interface ShoppingStoreService {
    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProduct(UUID productId);

    boolean updateQuantityState(UUID productId, QuantityState quantityState);

    ProductDto getProductById(UUID productId);
}
