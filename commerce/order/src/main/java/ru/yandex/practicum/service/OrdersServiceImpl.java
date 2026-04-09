package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.DeliveryFeignClient;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrdersState;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.mapper.OrdersMapper;
import ru.yandex.practicum.model.Orders;
import ru.yandex.practicum.repository.OrdersRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrdersServiceImpl implements OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final WarehouseFeignClient warehouseFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    @Override
    public List<OrdersDto> getOrders(String username) {
        checkUser(username);
        log.info("Get orders for user {}", username);
        List<Orders> orders = ordersRepository.findAllByUsername();
        return orders.stream()
                .map(ordersMapper::toOrdersDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrdersDto createNewOrder(CreateNewOrderRequest ordersRequest) {
        log.info("Create new order {}", ordersRequest);

        BookedProductsDto bookedProductsDto;
        try {
            log.info("Checking products at warehouse");
            bookedProductsDto = warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(ordersRequest.getShoppingCart());
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(e.getMessage());
            } else if (e.status() == 404) {
                throw new NoSpecifiedProductInWarehouseException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }

        Orders newOrders = ordersMapper.toOrders(ordersRequest, bookedProductsDto);
        newOrders = ordersRepository.save(newOrders);

        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setFromAddress(warehouseFeignClient.getWarehouseAddress());
        deliveryDto.setToAddress(ordersRequest.getAddress());
        deliveryDto.setOrderId(newOrders.getOrderId());
        deliveryDto.setDeliveryState(DeliveryState.CREATED);

        DeliveryDto newDeliveryDto = deliveryFeignClient.create(deliveryDto);

        newOrders.setDeliveryId(newDeliveryDto.getDeliveryId());
        newOrders = ordersRepository.save(newOrders);
        log.info("New order {} created", newOrders);
        return ordersMapper.toOrdersDto(newOrders);
    }

    @Override
    public OrdersDto returnOrder(ProductReturnRequest returnRequest) {
        log.info("Return order {}", returnRequest);
        Orders returnOrders = getOrdersById(returnRequest.getOrderId());
        Map<UUID, Integer> returnProducts = returnRequest.getProducts();
        try {
            log.info("Returning products to warehouse");
            warehouseFeignClient.returnProducts(returnProducts);
        } catch (FeignException e) {
                throw new RuntimeException(e.getMessage());
            }
        returnOrders = changeOrdersState(returnOrders, OrdersState.PRODUCT_RETURNED);
        return ordersMapper.toOrdersDto(returnOrders);
    }

    @Override
    public OrdersDto payment(UUID orderId) {
        log.info("Payment order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.PAID);
        AssemblyProductsForOrderRequest assemblyRequest
                = new AssemblyProductsForOrderRequest(orders.getProducts(),orderId);
        warehouseFeignClient.assemblyProducts(assemblyRequest);


        return null;
    }

    @Override
    public OrdersDto paymentFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto delivery(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto deliveryFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto complete(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto calculateTotalCost(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto calculateDeliveryCost(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto assembly(UUID orderId) {
        return null;
    }

    @Override
    public OrdersDto assemblyFailed(UUID orderId) {
        return null;
    }

    private void checkUser(String username) {
        log.info("Check user {}", username);
        if (username.isBlank()) {
           throw new NotAuthorizedUserException(username);
        }
    }

    private Orders getOrdersById(UUID orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order " + orderId + " does not exist"));
    }

    private Orders changeOrdersState(Orders orders, OrdersState ordersState) {
        orders.setOrderState(ordersState);
        orders = ordersRepository.save(orders);
        return orders;
    }
}
