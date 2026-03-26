package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.ProductState;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Get products for category {}", category);

        Page<Product> productPage;
        if (category != null) {
            productPage = productRepository.findAllByProductCategory(category, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductDto> productDtos = productPage.getContent().stream()
                .map(productMapper::toProductDto)
                .toList();

        return new PageImpl<>(productDtos, pageable, productPage.getTotalElements());
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        productRepository.save(product);
        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (!productRepository.existsById(productDto.getProductId())) {
            throw new ProductNotFoundException("Product id " + productDto.getProductId() + " doesn't exists");
        }
        Product product = productRepository.save(productMapper.toProduct(productDto));
        return productMapper.toProductDto(product);
    }

    @Override
    public boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product id " + productId + " doesn't e"));
        product.setQuantityState(quantityState);
        productRepository.save(product);
        log.info("Update quantity state {}", quantityState);
        return true;
    }

    @Override
    public boolean removeProduct(UUID productId) {
      log.info("Remove product {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id " + productId + " не существует"));
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
       log.info("Get product {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product id " + productId + " doesn't exists"));
        log.info("Get product {}", product);
        return productMapper.toProductDto(product);
    }
}