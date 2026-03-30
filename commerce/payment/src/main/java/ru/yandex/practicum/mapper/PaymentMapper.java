package ru.yandex.practicum.mapper;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "totalPayment", source = "ordersDto.totalPrice")
    @Mapping(target = "deliveryTotal", source = "ordersDto.deliveryPrice")
    @Mapping(target = "feeTotal", source = "feeTotal")
    @Mapping(target = "productTotal", source = "ordersDto.productPrice")
    @Mapping(target = "paymentState", ignore = true)
    @Mapping(target = "orderId", source = "ordersDto.orderId")
    Payment toPayment(OrdersDto ordersDto, BigDecimal feeTotal);

    PaymentDto toPaymentDto(Payment payment);
}