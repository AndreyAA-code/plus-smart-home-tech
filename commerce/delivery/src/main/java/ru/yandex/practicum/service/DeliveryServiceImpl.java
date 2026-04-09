package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private static final BigDecimal BASE_COST = BigDecimal.valueOf(5.0);
    private static final BigDecimal ADDRESS_1_FEE = BigDecimal.valueOf(1.0);
    private static final BigDecimal ADDRESS_2_FEE = BigDecimal.valueOf(2.0);


    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final AddressMapper addressMapper;
    private final WarehouseFeignClient warehouseFeignClient;


    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {
        log.info("Create delivery {}", deliveryDto);
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    public void successful(UUID orderId) {

    }

    @Override
    public void picked(UUID orderId) {

    }

    @Override
    public void failed(UUID orderId) {

    }

    @Override
    public BigDecimal cost(OrdersDto orderDto) {
        log.info("Order delivery cost {}", orderDto);
        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException ("delivery not found"));

        Address warehouseAddress = delivery.getFromAddress();
        Address destinationAddress = delivery.getToAddress();

        BigDecimal totalCost = BASE_COST;

        if (warehouseAddress.getFullAddress().contains("ADDRESS_1")) {
            totalCost = totalCost.multiply(ADDRESS_1_FEE).add(totalCost);
        } else if (warehouseAddress.getFullAddress().contains("ADDRESS_2")) {
            totalCost = totalCost.multiply(ADDRESS_2_FEE).add(totalCost);
        } else {
            totalCost = totalCost.add(totalCost);
        }



       log.info("Delivery cost {}", totalCost);
        return totalCost;
    }

    private boolean warehouseAddressContains(Address address, String substring) {
        return address != null
                && address.getFullAddress() != null
                && address.getFullAddress().contains(substring);
    }
}
