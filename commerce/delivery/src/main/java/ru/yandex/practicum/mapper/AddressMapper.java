package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AddressMapper {

    @Mapping(target = "addressId", ignore = true)
    Address toAddress(AddressDto addressDto);

    AddressDto toDeliveryDto(Address address);

}
