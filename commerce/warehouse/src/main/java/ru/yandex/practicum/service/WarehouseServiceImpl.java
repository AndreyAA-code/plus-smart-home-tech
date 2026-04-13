package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.api.ShoppingStoreFeignClient;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AddressDto warehouseAddress = createAddress();
    private final ShoppingStoreFeignClient shoppingStoreClient;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("Add new product to warehousee");
        warehouseRepository.findById(request.getProductId())
                .ifPresent(product -> {
                    log.warn("Product with ID: {} already exists", request.getProductId());
                    throw new SpecifiedProductAlreadyInWarehouseException("Product is already in warehouse");
                });
        warehouseRepository.save(warehouseMapper.toEntity(request));
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        Map<UUID, Integer> products = cartDto.getProducts();
        log.info("Query for quantity of products at warehouse {}", products.keySet());
        List<WarehouseProduct> availableProductsList = warehouseRepository.findAllById(products.keySet());
        Map<UUID, WarehouseProduct> availableProductsMap = availableProductsList.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        for (Map.Entry<UUID, Integer> product : products.entrySet()) {
            UUID id = product.getKey();
            WarehouseProduct availableProduct = availableProductsMap.get(id);
            if (availableProduct == null) {
                throw new NoSpecifiedProductInWarehouseException("No such product at warehouse"
                        + product.getKey().toString());
            }
            if (availableProduct.getQuantity() >= product.getValue()) {
                Double volume = bookedProductsDto.getDeliveryVolume() + (availableProduct.getWidth()
                        * availableProduct.getHeight() * availableProduct.getDepth()) * product.getValue();
                bookedProductsDto.setDeliveryVolume(volume);
                Double weight = bookedProductsDto.getDeliveryWeight() + (availableProduct.getWeight())
                        * product.getValue();
                bookedProductsDto.setDeliveryWeight(weight);
                if (availableProduct.getFragile()) {
                    bookedProductsDto.setFragile(true);
                }
            } else {
                String message = "Quantity of " + availableProduct.getProductId() + " " +
                        "is not enough& Reduce the quantity " + availableProduct.getQuantity();
                log.info(message);
                throw new ProductInShoppingCartLowQuantityInWarehouse(message);
            }
        }
        return bookedProductsDto;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Add product to warehouse request {}", request);
        WarehouseProduct product = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("No such product at warehouse"
                        + request.getProductId()));
        Integer oldQuantity = product.getQuantity();
        Integer newQuantity = oldQuantity + request.getQuantity();
        product.setQuantity(newQuantity);

        ProductDto productDto;
        try {
            productDto = shoppingStoreClient.getProduct(product.getProductId());
            QuantityState quantityState = QuantityState.fromQuantity(newQuantity);
            shoppingStoreClient.setProductQuantityState(product.getProductId(), quantityState);
            log.info("Quantity updated for warehouse product {}", productDto);
        } catch (RuntimeException e) {
            log.info("No such product at warehouse");
        }
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseAddress;
    }

    @Override
    public void returnProductToWarehouse(Map<UUID, Integer> returnProducts) {
        Set<UUID> ids = returnProducts.keySet();
        for (UUID id : ids) {
            AddProductToWarehouseRequest addProductToWarehouseRequest = new AddProductToWarehouseRequest(id, returnProducts.get(id));
            addProductToWarehouse(addProductToWarehouseRequest);
            log.info("Products returned to warehouse");
        }
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        log.info("Assembly products request {}", request);
        Map<UUID, Integer> products = request.getProducts();
        List<WarehouseProduct> availableProductsList = warehouseRepository.findAllById(products.keySet());
        Map<UUID, WarehouseProduct> availableProductsMap = availableProductsList.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        BookedProductsDto bookedProducts = new BookedProductsDto();
        List<WarehouseProduct> productsToUpdate = new ArrayList<>();
        for (Map.Entry<UUID, Integer> product : products.entrySet()) {
            UUID id = product.getKey();
            Integer requestedQuantity = product.getValue();
            WarehouseProduct availableProduct = availableProductsMap.get(id);
            log.info("Check product availability for: {}", availableProduct);
            checkProductAvailability(availableProduct, requestedQuantity);

            log.info("Update booked product dimensions and fragile for: {}", availableProduct);
            updateBookedProducts(bookedProducts, availableProduct, requestedQuantity);

            log.info("Reduce quantity at warehouse for {}", availableProduct);
            availableProduct.setQuantity(availableProduct.getQuantity() - requestedQuantity);
            productsToUpdate.add(availableProduct);
        }

        warehouseRepository.saveAll(productsToUpdate);
        log.info("Products assembled at warehouse");

        return bookedProducts;
    }

    private AddressDto createAddress() {
        String[] addresses = {"ADDRESS_1", "ADDRESS_2"};
        Random random = new Random();
        String address = addresses[random.nextInt(2)];

        return AddressDto.builder()
                .city(address)
                .street(address)
                .house(address)
                .country(address)
                .flat(address)
                .build();
    }

    private void checkProductAvailability(WarehouseProduct product, Integer requestedQuantity) {
        if (product == null) {
            throw new NoSpecifiedProductInWarehouseException("No such product at warehouse");
        }
        if (product.getQuantity() < requestedQuantity) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Not enough product at warehouse");
        }
    }

    private void updateBookedProducts(BookedProductsDto dto, WarehouseProduct product, Integer quantity) {
        Double volume = product.getWidth() * product.getHeight() * product.getDepth() * quantity;
        dto.setDeliveryVolume(dto.getDeliveryVolume() + volume);

        Double weight = product.getWeight() * quantity;
        dto.setDeliveryWeight(dto.getDeliveryWeight() + weight);

        if (product.getFragile()) {
            dto.setFragile(true);
        }
    }

}
