package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShoppingCartMapper {

    @Mapping(target = "cartId", source = "shoppingCartId")
    ShoppingCart toCart(ShoppingCartDto cartDto);

    @Mapping(target = "shoppingCartId", source = "cartId")
    ShoppingCartDto toCartDto(ShoppingCart cart);
}