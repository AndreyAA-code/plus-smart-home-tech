package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.DeliveryFeignClient;
import ru.yandex.practicum.api.PaymentFeignClient;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrdersState;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.mapper.OrdersMapper;
import ru.yandex.practicum.model.Orders;
import ru.yandex.practicum.repository.OrdersRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    private final PaymentFeignClient paymentFeignClient;

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
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto paymentFailed(UUID orderId) {
        log.info("Payment failed order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.PAYMENT_FAILED);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto delivery(UUID orderId) {
        log.info("Delivery order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.DELIVERED);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto deliveryFailed(UUID orderId) {
        log.info("Delivery failed order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.DELIVERY_FAILED);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto complete(UUID orderId) {
        log.info("Complete order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.COMPLETED);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto calculateTotalCost(UUID orderId) {
        log.info("Calculate total cost {}", orderId);
        Orders orders = getOrdersById(orderId);
        BigDecimal totalCost = paymentFeignClient.getTotalCost(ordersMapper.toOrdersDto(orders));
        orders.setTotalPrice(totalCost);

        PaymentDto paymentDto = paymentFeignClient.createPayment(ordersMapper.toOrdersDto(orders));
        orders.setPaymentId(paymentDto.getPaymentId());

        orders = ordersRepository.save(orders);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto calculateDeliveryCost(UUID orderId) {
        log.info("Calculate delivery cost for order {}", orderId);
        Orders orders = getOrdersById(orderId);
        BigDecimal productCost = paymentFeignClient.productCost(ordersMapper.toOrdersDto(orders));
        orders.setProductPrice(productCost);
        BigDecimal deliveryCost = deliveryFeignClient.deliveryCost(ordersMapper.toOrdersDto(orders));
        orders.setDeliveryPrice(deliveryCost);

        orders = ordersRepository.save(orders);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto assembly(UUID orderId) {
        log.info("Assembling order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.ASSEMBLED);
        return ordersMapper.toOrdersDto(orders);
    }

    @Override
    public OrdersDto assemblyFailed(UUID orderId) {
        log.info("Assembling failed order {}", orderId);
        Orders orders = getOrdersById(orderId);
        orders = changeOrdersState(orders, OrdersState.ASSEMBLY_FAILED);
        return ordersMapper.toOrdersDto(orders);
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
