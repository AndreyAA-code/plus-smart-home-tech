package ru.yandex.practicum.mapper;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {

    @Mapping(target = "paymentId", ignore = true)
    Payment toPayment(PaymentDto paymentDto);

    PaymentDto toPaymentDto(Payment payment);
}