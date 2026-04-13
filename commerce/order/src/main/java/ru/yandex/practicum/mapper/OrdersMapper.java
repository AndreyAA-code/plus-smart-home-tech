package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.Orders;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrdersMapper {

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "products", expression = "java(request.getShoppingCart().getProducts())")
    @Mapping(target = "shoppingCartId", source = "request.shoppingCart.shoppingCartId")
    @Mapping(target = "deliveryWeight", source = "bookedProductsDto.deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "bookedProductsDto.deliveryVolume")
    @Mapping(target = "fragile", source = "bookedProductsDto.fragile")
    @Mapping(target = "username", source = "request.username")
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "orderState", constant = "NEW")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deliveryPrice", ignore = true)
    @Mapping(target = "productPrice", ignore = true)
    Orders toOrders(CreateNewOrderRequest request, BookedProductsDto bookedProductsDto);

    OrdersDto toOrdersDto(Orders orders);
}